package mario.Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import mario.ImageBox;
import mario.Server.Protocols;
import mario.dto.BalloonDTO;
import mario.dto.MarioDTO;

public class MarioClient extends JFrame implements ActionListener{

	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	/* MarioLogin 클래스에서 접속시 들어오는 플레이어 정보 dto  */
	MarioDTO clientData;

//	/* 서버로부터 받아올 객체  */
//	static List<MarioDTO> list_PlayerInfo;
	
	
	/* 필드 객체  */
	
	private MarioCanvas marioCanvas;
	private MarioLogin marioLogin;
	private String nickname = "guest";
	public static JTextArea textArea_Chat;
	public static JTextField textField_Chat;
	private JLabel label_Timer_Minute, label_Timer_Second, label_Timer_MiliSec, label_Elapsed_Time;

	private JTable scoreTable;
	public DefaultTableModel model;
	public JButton btn_send, btn_start;
	
	/* 타이머  */
	Thread timerThread;
	public boolean timerStart = false;
	private int timer_Minute = 0;
	private int timer_Second = 0;
	private int timer_MiliSec = 0;
	
	

	/**********************************************************************************************/

	//getter
	
	
	/**********************************************************************************************/
	
	
	
	// 생성자
	
	public MarioClient(MarioLogin marioLogin) {
		super("Mario");
		this.marioLogin = marioLogin;
		this.clientData = marioLogin.clientData;
		
		showDTOdata();
		
		/* 다른 클래스 생성 */
		new ImageBox();
		marioCanvas = new MarioCanvas(MarioClient.this, marioLogin);

		
		
		/* ******************************************************************* */
		// 표시될 객체 생성

		/* 라벨 */
		label_Timer_Second = new JLabel("00 : ");
		label_Timer_Minute = new JLabel("00 : ");
		label_Elapsed_Time = new JLabel("");
		label_Timer_MiliSec = new JLabel("00ms");

		/* 채팅창 텍스트필드 */
		textField_Chat = new JTextField("                   Press Enter");
		textField_Chat.setBackground(new Color(240, 240, 240));
		textArea_Chat = new JTextArea();
		textArea_Chat.setEditable(false);
		textArea_Chat.setLineWrap(true);
		
		/* 채팅창 스크롤 */
		JScrollPane scroll_chat = new JScrollPane(textArea_Chat);

		/* 버튼 */
		btn_start = new JButton("게임생성");
		btn_send = new JButton("Send");
		btn_start = new JButton("시작");

		/* JTable 랭킹창 */
		Vector<String> vector = new Vector<String>();
		vector.add("Rank");
		vector.add("Name");
		vector.add("Complete");
		vector.add("Flag");
		/* 랭킹, 닉네임, 진행도, 깃발 획득횟수 */

		model = new DefaultTableModel(vector, 0);
		scoreTable = new JTable(model);
		scoreTable.getColumn("Rank").setPreferredWidth(55);
		scoreTable.getColumn("Flag").setPreferredWidth(35);
		scoreTable.setEnabled(false);
		JScrollPane jsc = new JScrollPane(scoreTable);

		/* ******************************************************************* */

		// 객체 설정

		label_Timer_Minute.setFont(new Font("MD개성체", Font.BOLD, 20));
		label_Timer_Second.setFont(new Font("MD개성체", Font.BOLD, 20));
		label_Elapsed_Time.setFont(new Font("MD개성체", Font.BOLD, 20));
		label_Timer_MiliSec.setFont(new Font("MD개성체", Font.BOLD, 20));

		/* 랭킹창 사이즈 */
		jsc.setPreferredSize(new Dimension(250, 200));

		scroll_chat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		/* 채팅창하단 패널 */
		JPanel p_s_text = new JPanel(new BorderLayout());
		p_s_text.add("Center", textField_Chat);
		p_s_text.add("East", btn_send);
		p_s_text.setBounds(200, 400, 400, 300);

		/* 채팅창 패널 */
		JPanel p_text = new JPanel(new BorderLayout());
		p_text.add("South", p_s_text);
		p_text.add("Center", scroll_chat);

		/* 점수 패널 */
		JPanel p_score = new JPanel(new BorderLayout());
		p_score.add("Center", p_text);
		p_score.add("South", btn_start);
		p_score.add("North", jsc); // 스크롤넣음

		/* 상단 패널 */
		JPanel northPanel = new JPanel(new BorderLayout());

		/* 타이머 패널 */
		JPanel panel_Timer = new JPanel(new FlowLayout());
		panel_Timer.add(label_Elapsed_Time);
		panel_Timer.add(label_Timer_Minute);
		panel_Timer.add(label_Timer_Second);
		panel_Timer.add(label_Timer_MiliSec);
		
		panel_Timer.setBackground(Color.ORANGE);
		panel_Timer.setPreferredSize(new Dimension(250, 30));

		northPanel.add("East", panel_Timer);

		add("East", p_score);
		add("Center", marioCanvas);
		add("North", northPanel);

		/* 윈도우 창 설정 */
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(null);

		
		/* ******************************************************************* */
		// 이벤트
		
		
		btn_start.addActionListener(this);
		btn_send.addActionListener(this);

		/* 윈도우 닫기 이벤트 */
		textField_Chat.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {

				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					btn_send.doClick();
					marioCanvas.chatOpen = false;

					
				}
				
			}
		});
		
		textField_Chat.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
				textField_Chat.setBackground(new Color(240, 240, 240));
				textField_Chat.setText("                   Press Enter");
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
				MarioClient.textField_Chat.setText("");
				MarioClient.textField_Chat.setBackground(new Color(255,255,255));
				
			}
		});
		
		
		/* ******************************************************************* */
		// 윈도우 창 닫기 이벤트
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				int result = JOptionPane.showConfirmDialog(MarioClient.this, "정말 갈꺼에요? (•́︿•̀｡) ", "가지마",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (result == JOptionPane.YES_OPTION && MarioLogin.serverConnected) {

					MarioDTO dto = new MarioDTO();
					dto.setProtocol(Protocols.EXIT);
					dto.setSeq(clientData.getSeq());
					dto.setNickname(clientData.getNickname());
					
					try {
						
						marioLogin.oos.writeObject(dto);
						marioLogin.oos.flush();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				} else if (result == JOptionPane.YES_OPTION && MarioLogin.serverConnected) {

					System.exit(0);
					
				} else if (result == JOptionPane.NO_OPTION) {

					return;
				}
			}
		});
		
		/* ******************************************************************* */
		// 타이머 스레드
		
		
		timerThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				
				while(timerStart) {
						/* ******************************************************************* */
						
						runTimer();
						
						/* ******************************************************************* */
						try {
							Thread.sleep(33); // 1초에 30번 반복
						} catch (InterruptedException e) {
				
							e.printStackTrace();
						}
					}
			} // run();
		}); // timerThread;
		
		
		/* ******************************************************************* */

	} // MarioClient();
	
	
	

	
	/********************************************************************************************/
	// 버튼 이벤트

	@Override
	public void actionPerformed(ActionEvent e) {

		
		
		/* ******************************************************************* */
		// 시작 버튼
		
		if (e.getSource() == btn_start) {

			if(!timerStart) {
				timerStart = true;
				
				timer_MiliSec = 0;
				timer_Second = 0;
				timer_Minute = 0;
					
				/* 접속여부 체크 후 서버로 정보를 송신하는 스레드 시작  */
				if(!timerThread.isAlive()) {
					
				timerThread.start();
				}
			
				btn_start.setVisible(false);
			}
			
		}// if;
		
		
		/* ******************************************************************* */
		// 채팅 보내기
		
		else if( e.getSource() == btn_send) {
			
			/* 입력값이 없으면 보내지 않는다. */
			if(textField_Chat.getText().length() == 0){
				
				marioCanvas.requestFocus();
				return;
		
			}else {
			MarioDTO dto = new MarioDTO();
			dto.setProtocol(Protocols.SEND);
			dto.setNickname(clientData.getNickname());
			dto.setChatMessage(textField_Chat.getText());
			
			marioCanvas.list_Balloon.add(new BalloonDTO(122, 1, textField_Chat.getText()));
			
			/* 캐릭터 2는 랜덤챗  */
			int ran = (int)(Math.random() * 5);
			String text = "";
			
			if(ran == 0) {
				text += "왜";
			}else if (ran == 1) {
				text += "뭐";
			}else if (ran == 2) {
				text += "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ";
			}else if (ran == 3) {
				text += "야 꺼져";
			}else if (ran == 4) {
				text += "하 살기 힘들다";
			}else {
				text = "호잇";
			}
			marioCanvas.list_Balloon.add(new BalloonDTO(122, 2, text));
			
			textField_Chat.setText("");
			
			
				try {
					marioLogin.oos.writeObject(dto);
					marioLogin.oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			/* 채팅 전송 후, 캔버스가 포커스를 되찾는다. */
			marioCanvas.requestFocus();
			
		} // else if;

	} // actionPerformed;
	
	
	
	/********************************************************************************************/
	// 타이머 메소드
	
	private void runTimer() {
		
		if(timerStart) {
		timer_MiliSec += 33; // 0.033초
		}
		
		if(timer_MiliSec >= 1000) {
			timer_MiliSec -= 1000;
			timer_Second++;
		}
		
		if(timer_Second >= 60) {
			timer_Second -= 60;
			timer_Minute++;
		}
		
		label_Timer_MiliSec.setText(new DecimalFormat("00").format(timer_MiliSec / 10) + "ms");
		label_Timer_Second.setText(new DecimalFormat("00").format(timer_Second) + " : ");
		label_Timer_Minute.setText(new DecimalFormat("00").format(timer_Minute) + " : ");
		
		
	}
	
	
	/********************************************************************************************/
	

	
	/********************************************************************************************/

	// 로그인할때 회원 정보 출력
	
	private void showDTOdata() {
		
		if(clientData != null) {
			System.out.println();
			System.out.println("**********************************");
			System.out.println("	[접속한 클라이언트 정보]");
			System.out.println("seq : " + clientData.getSeq());
			System.out.println("Nickname : " + clientData.getNickname());
			System.out.println("Age : " + clientData.getAge());
			System.out.println("ClientAccount : " + clientData.getClientAccount());
			System.out.println("Gender : " + clientData.getGender());
			System.out.println("Password : " + clientData.getPassword());
			System.out.println("PasswordCheck : " + clientData.getPassword());
			System.out.println("**********************************");
			System.out.println();
		}else {
			System.out.println(clientData);
		}
	}
	
	/********************************************************************************************/

	
	public static void main(String[] args) {
		new MarioLogin();
	}
	
	
}
