package mario.Frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import mario.EmailAutho;
import mario.Server.Protocols;
import mario.dao.MarioDAO;
import mario.dto.MarioDTO;

public class MarioSignup extends JFrame implements ActionListener, Runnable {

	// 라벨
	private JLabel label_signup_info, label_emailAccount, label_pwd, label_pwdCheck, label_nick_name, label_name, label_age,
			label_gender, label_atMark, label_timer, label_pwdChkLabel, label_NicknameChkLabel; 

	private JTextField tf_emailAccount, tf_nickname, tf_name, tf_age, tf_emailAuth;
	private JPasswordField tf_pwd, tf_pwd_check; // 비밀번호 암호화

	private JCheckBox cb_person_info, checkBox_man, checkBox_woman; // 개인정보 취급방침에 대해 동의합니다
	private JButton btn_EmailAuth, btn_nickname_check, btn_signup, btn_cancel, btn_AuthCancel;

	private JComboBox<String> comboBox_email; // 네이버 이메일선택
	private ImageIcon ic_bg_signup = new ImageIcon("image/background/signupBackground.png"); // 배경아이콘
	private Image im_bg_signup = ic_bg_signup.getImage();

	private DefaultTableModel model;
	private Vector<String> vector;

	private List<MarioDTO> dtoList;
	
	// 타이머
	private boolean timerStart = false;
	private int min = 3, sec = 0;
	private boolean AuthSuccess = false;
	private Thread timerThread;
	private boolean threadStop = false;
	
	//메일 인증
	private int authCode;
	
	// 닉네임 인증
	private boolean nicknameCheck = false;
	
	MarioLogin marioLogin;
	
	
	/***************************************************************************************************************************/

	
	// 생성자
	
	public MarioSignup(MarioLogin marioLogin) {
		super("입소신청서");
		
		this.marioLogin = marioLogin;
		this.dtoList = MarioLogin.dtoList;
		
		// 윈도우 창 설정
		
		setBounds(0, 0, 550, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUndecorated(false);
		setAlwaysOnTop(true);

		
		/* ******************************************************************* */

		// 항목 생성
		
			// 라벨
			label_signup_info = new JLabel("마리오 회원가입");
			label_emailAccount = new JLabel("Email 계정");
			label_pwd = new JLabel("비밀 번호");
			label_pwdCheck = new JLabel("비밀번호확인");
			label_nick_name = new JLabel("닉 네 임");
			label_name = new JLabel("이    름 ");
			label_age = new JLabel("나    이");
			label_gender = new JLabel("성    별");
			label_atMark = new JLabel("@");
			label_timer = new JLabel("남은시간 : " + min + " : " + new DecimalFormat("00").format(sec));
			label_pwdChkLabel = new JLabel();
			label_NicknameChkLabel = new JLabel("이메일을 먼저 인증해주세요");

			// 텍스트필드
			tf_emailAccount = new JTextField(20);
			comboBox_email = new JComboBox<String>(new String[]{"naver.com", "gmail.com"});
			tf_pwd = new JPasswordField(20);
			tf_pwd_check = new JPasswordField(20);
			tf_nickname = new JTextField(20);
			tf_name = new JTextField(20);
			tf_age = new JTextField(20);
			tf_emailAuth = new JTextField("입력 후 엔터", 20);

			// 버튼
			btn_EmailAuth = new JButton("인증받기");
			btn_signup = new JButton("회원가입");
			btn_cancel = new JButton("취소");
			btn_nickname_check = new JButton("Use");
			btn_AuthCancel = new JButton("인증 취소");
		
			// 성별 체크박스 & 버튼 그룹
			ButtonGroup group_gender = new ButtonGroup();
			checkBox_man = new JCheckBox("남성", true);
			checkBox_woman = new JCheckBox("여성");
			group_gender.add(checkBox_man);
			group_gender.add(checkBox_woman);

			// 개인정보 수락 체크박스
			cb_person_info = new JCheckBox("개인정보 취급방침에 대해 동의합니다");

			// 테두리속없애기
			cb_person_info.setBorder(BorderFactory.createEtchedBorder());
			checkBox_man.setBorder(BorderFactory.createEtchedBorder());
			checkBox_woman.setBorder(BorderFactory.createEtchedBorder());

			// 버튼이 선택(focus)되었을때 생기는 테두리 사용안함
			cb_person_info.setFocusable(false);
			btn_cancel.setFocusable(false);
			btn_signup.setFocusable(false);
			btn_nickname_check.setFocusable(false);
			btn_EmailAuth.setFocusable(false);
			checkBox_woman.setFocusable(false);
			checkBox_man.setFocusable(false);
			btn_AuthCancel.setFocusable(false);

			// 폰트
			label_signup_info.setFont(new Font("MD개성체", Font.BOLD, 20));
			label_emailAccount.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_pwd.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_pwdCheck.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_nick_name.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_name.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_age.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_gender.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_atMark.setFont(new Font("MD개성체", Font.BOLD, 15));
			label_timer.setFont(new Font("MD개성체", Font.BOLD, 13));
			label_pwdChkLabel.setFont(new Font("MD개성체", Font.BOLD, 13));
			label_NicknameChkLabel.setFont(new Font("MD개성체", Font.BOLD, 13));
			btn_AuthCancel.setFont(new Font("MD개성체", Font.BOLD, 11));
			
			// 글자 색
			tf_pwd.setForeground(Color.BLACK);
			tf_emailAccount.setForeground(Color.BLACK);
			label_atMark.setForeground(Color.BLACK);
			btn_signup.setBackground(Color.getHSBColor(150, 200, 240));
			btn_cancel.setBackground(Color.getHSBColor(150, 200, 240));
			tf_emailAuth.setForeground(new Color(170, 170, 170));
			label_timer.setForeground(new Color(240, 100, 100));
			label_pwdChkLabel.setForeground(new Color(250, 50, 50));
			
			
			/* ******************************************************************* */

			// 라벨좌표
			int x = 40, y = 60; //기준 좌표
			
			/* 마리오 회원가입 배너 */
			label_signup_info.setBounds				(x + 310, 	y, 		 	200, 20);
			
			/* 이메일 */
			label_emailAccount.setBounds			(x,		 	y + 100, 	120, 20);
			tf_emailAccount.setBounds				(x + 100,	y + 95, 	110, 28);
			label_atMark.setBounds					(x + 216,	y + 100,    30,  20);
			comboBox_email.setBounds				(x + 240, 	y + 95, 	100, 28);
			
			/* 이메일 인증  */
			btn_EmailAuth.setBounds					(x + 345,	y + 95,		90, 28);
			tf_emailAuth.setBounds					(x + 345,	y + 95,		100, 28);
			label_timer.setBounds					(x + 330,	y + 125,	160, 20);
			btn_AuthCancel.setBounds				(x + 359,	y + 65 ,	85, 25);
			
			/* 비밀번호 */
			label_pwd.setBounds						(x, 		y + 140,	120, 20);
			tf_pwd.setBounds						(x + 100, 	y + 135,	110, 28);
			
			/* 비밀번호 확인*/
			label_pwdCheck.setBounds				(x,			y + 180,	120, 20);
			tf_pwd_check.setBounds					(x + 100, 	y + 175, 	110, 28);
			label_pwdChkLabel.setBounds				(x + 215,	y + 180,	200, 20);				
			
			/* 닉네임 */
			label_nick_name.setBounds				(x, 		y + 220, 	120, 20);
			tf_nickname.setBounds					(x + 100, 	y + 215, 	110, 28);
			btn_nickname_check.setBounds			(x + 213, 	y + 215, 	69, 27); 
			label_NicknameChkLabel.setBounds		(x + 285, 	y + 220, 	230, 20);
			
			/* 실명 */
			label_name.setBounds					(x, 		y + 260, 	120, 20);
			tf_name.setBounds						(x + 100, 	y + 255, 	110, 28);
			
			/* 나이 */
			label_age.setBounds						(x, 		y + 300, 	120, 20);
			tf_age.setBounds						(x + 100, 	y + 295, 	110, 28);
			
			/* 성별  */
			label_gender.setBounds					(x, 		y + 340, 	120, 20);
			checkBox_man.setBounds					(x + 105,	y + 340,	50, 20);
			checkBox_woman.setBounds				(x + 170, 	y + 340, 	50, 20);
			
			/* 개인정보 동의 */
			cb_person_info.setBounds				(x + 100, 	y + 375, 	250, 30);
		
			/* 가입 & 취소 */
			btn_signup.setBounds					(x + 95, 	y + 420, 	100, 40);
			btn_cancel.setBounds					(x + 205, 	y + 420, 	100, 40);
		
			
		
			/* ******************************************************************* */
			
		// 배경화면
//		JPanel background = new JPanel() {
//			public void paintComponent(Graphics g) {
//				g.drawImage(im_bg_signup, 0, 0, null);
//			}
//		};

		/* JPanel 생성 */
		JPanel background = new JPanel(null) {
			public void paintComponent(Graphics g) {
				g.drawImage(im_bg_signup, 0, 0, null);
			}
		};

		
		/* JPanel에 항목 추가 */
		background.add(label_signup_info);
		background.add(label_emailAccount);
		background.add(label_pwd);
		background.add(label_pwdCheck);
		background.add(label_nick_name);
		background.add(label_name);
		background.add(label_age);
		background.add(label_gender);
		background.add(label_atMark);
		background.add(label_emailAccount);
		
		background.add(label_NicknameChkLabel);
		label_NicknameChkLabel.setVisible(false); // 보이지 않는 채로 생성
		
		background.add(label_timer);
		label_timer.setVisible(false);
		
		background.add(label_pwdChkLabel);
		label_pwdChkLabel.setVisible(false);
		
		background.add(tf_emailAccount);
		
		background.add(tf_emailAuth);
		tf_emailAuth.setVisible(false);
		
		background.add(btn_AuthCancel);
		btn_AuthCancel.setVisible(false);
		
		background.add(tf_pwd);
		background.add(tf_pwd_check);
		background.add(tf_nickname);
		background.add(tf_name);
		background.add(tf_age);

		background.add(comboBox_email);
		background.add(cb_person_info);

		background.add(btn_cancel);
		background.add(btn_signup);
		background.add(btn_EmailAuth);
		background.add(btn_nickname_check);
		btn_nickname_check.setVisible(false);
		
		background.add(checkBox_man);
		background.add(checkBox_woman);

		add(background);

		
		
		/* ******************************************************************* */

		//스레드 생성 및 시작
		
		timerThread = new Thread(this);
		timerThread.start();
		
		
		/* ******************************************************************* */
		
		// 이벤트
		
		btn_signup.addActionListener(this);
		btn_cancel.addActionListener(this);
		btn_EmailAuth.addActionListener(this);
		btn_nickname_check.addActionListener(this);
		tf_emailAuth.addActionListener(this);
		btn_AuthCancel.addActionListener(this);
		
		/* 클릭하면 텍스트필드가 공백이 되는 이벤트 */
		tf_emailAuth.addFocusListener( new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				tf_emailAuth.setText("");
				tf_emailAuth.setForeground(Color.BLACK);
				
			}
		});
		/* 닉네임 중복검사 */
		tf_nickname.addFocusListener( new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
				btn_nickname_check.setVisible(false);
				
				/* 이메일 인증이 완료되기 전에는 닉네임 중복검사는 동작하지 않는다. */
				if (!AuthSuccess) {
					label_NicknameChkLabel.setForeground(new Color(200, 50, 50));
					label_NicknameChkLabel.setText("이메일 인증을 먼저 완료해주세요.");
					label_NicknameChkLabel.setVisible(true);
					return;

					/* 이메일 인증시에 받은 dtoList로 닉네임 비교 */
				} else {

					if (tf_nickname.getText().length() < 3 || tf_nickname.getText() == null) {

						label_NicknameChkLabel.setForeground(new Color(250, 100, 0));
						label_NicknameChkLabel.setText("닉네임을 3자 이상 입력해주세요.");
						label_NicknameChkLabel.setVisible(true);
						nicknameCheck = false;
						return;
					}

					if (dtoList != null) {
						for (MarioDTO dto : MarioLogin.dtoList) {
							if (tf_nickname.getText().equals(dto.getNickname())) {
								label_NicknameChkLabel.setForeground(new Color(100, 50, 50));
								label_NicknameChkLabel.setText("해당 닉네임이 이미 존재합니다.");
								label_NicknameChkLabel.setVisible(true);
								nicknameCheck = false;

								return;
							}
						}
					}
					
						System.out.println(dtoList);
						label_NicknameChkLabel.setForeground(new Color(100, 200, 150));
						label_NicknameChkLabel.setText("해당 닉네임을 사용할 수 있습니다.");
						label_NicknameChkLabel.setVisible(true);
						btn_nickname_check.setVisible(true);
						nicknameCheck = true;
					
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				btn_nickname_check.setVisible(false);
				tf_nickname.setText("");
				nicknameCheck = false;
				label_NicknameChkLabel.setVisible(false);
				
			}
		});

		
		/* 창 닫기 이벤트 */
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(MarioSignup.this, "입력한 내용이 모두 사라집니다.", "종료창",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					dispose();

				} else if (result == JOptionPane.NO_OPTION) {

				}
			}
		});
		
		
		/* ******************************************************************* */
		
		
		setVisible(true);
		
	}// MarioSignup ()
	

	
	/*************************************************************************************************************/
	
	// 입력값 검사 및 DB에 저장 
	
	public void CheckArticle() {

		String error = "";
		
		/* 입력값이 없거나, 조건에 맞지 않으면 에러 */
		if(tf_emailAccount.getText().length() == 0 || tf_emailAccount.getText() == null) 								{ error += "이메일"; }
		else if(new String(tf_pwd.getPassword()).length() == 0 || new String(tf_pwd.getPassword()) == null) 			{ error += "비밀번호"; }
		else if(new String(tf_pwd_check.getPassword()).length() == 0 || new String(tf_pwd_check.getPassword()) == null) { error += "비밀번호 확인"; }
		else if(tf_nickname.getText().length() == 0 || tf_nickname.getText() == null || !nicknameCheck) 				{ error += "닉네임"; }
		else if(tf_name.getText().length() == 0 || tf_name.getText() == null)											{ error += "이름"; }
		else if(tf_age.getText().length() == 0 || tf_age.getText() == null || !tf_age.getText().matches("\\d*") ) 		{ error += "나이"; }
		else if(!AuthSuccess ) 		{ error += "인증키"; }
		
		if(error.length() != 0) {
			JOptionPane.showMessageDialog(this, error + " 입력란을 확인해주세요.");
			
			
		/* 입력된 값으로 회원가입 진행  */
		}else {

			/* DTO 객체 생성 및 입력 */
			MarioDTO signupdto = new MarioDTO();
			
			signupdto.setProtocol(Protocols.SIGNUP);
			signupdto.setClientAccount(tf_emailAccount.getText() + "@" + (String)comboBox_email.getSelectedItem());
			signupdto.setPassword(new String(tf_pwd.getPassword()));
			signupdto.setNickname(tf_nickname.getText());
			signupdto.setRealName(tf_name.getText());
			signupdto.setAge(Integer.parseInt(tf_age.getText()));
			signupdto.setGender(checkBox_man.isSelected() ? 0 : 1); 	// 여자 : 1
			signupdto.setInfoAgree(cb_person_info.isSelected() ? 1 : 0);
			signupdto.setScore(0);
			signupdto.setGoalTime(null);
			signupdto.setPlayerRank(0);
			
			try {
				marioLogin.oos.writeObject(signupdto);
				marioLogin.oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/* 접속 성공 후 메세지 출력, 스레드 종료, 창 끄기  */
			JOptionPane.showMessageDialog(this, signupdto.getNickname() + "님 환영합니다!");
			threadStop = true;
			dispose();
			
			marioLogin.fill_login_emailAccount(tf_emailAccount.getText(), comboBox_email.getSelectedIndex());
			
		}
		
	}
	
	/*************************************************************************************************************/
	
	
	// 버튼 이벤트
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		/* ******************************************************************* */

		// 회원가입 버튼 이벤트
		
		if (e.getSource() == btn_signup) {

				CheckArticle();
			
		} 
		
		/* ******************************************************************* */
		
		// 취소 버튼 이벤트
		
		else if (e.getSource() == btn_cancel) {
			
			int result = JOptionPane.showConfirmDialog(this, "입력한 내용이 모두 사라집니다.", "종료창",
					JOptionPane.YES_NO_OPTION);
			
			if (result == JOptionPane.YES_OPTION) {
				
				dispose();
			} 
			else if (result == JOptionPane.NO_OPTION) {
				
				return;
			}
			
		} 
			
		/* ******************************************************************* */

		// 이메일 인증 버튼 이벤트
		
		else if (e.getSource() == btn_EmailAuth) {
			
			/* 이메일 입력란이 빈칸일경우 */
			if(tf_emailAccount.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "이메일 입력란을 확인해주세요.");
				return;
			}
			
			
			/* 입력된 이메일에 나머지 주소를 입력시킨다. ( + @gmail.com) */
			String emailAccount = new String(tf_emailAccount.getText() + "@" + (String)comboBox_email.getSelectedItem());

			
			/* DB에서 전체 playerList를 받아 입력한 이메일과 비교  */
//			MarioDAO dao = MarioDAO.getInstance();
//			dtoList = dao.getMarioList();
			
			for (MarioDTO dto : MarioLogin.dtoList) { 
				
				if (emailAccount.equals(dto.getClientAccount())) {
					JOptionPane.showMessageDialog(this, "[ " + emailAccount + " ] \n 해당 계정은 이미 존재합니다.");
					return;
				}
			}
			
			/* 중복되는 계정이 없을 경우, */
		
			
			/* 이메일 입력란 비활성화, 이메일 인증버튼 사라짐  */
			tf_emailAccount.setEnabled(false); // 이메일 입력란 비활성화
			comboBox_email.setEnabled(false); // 콤보박스 비활성화
			btn_EmailAuth.setVisible(false);	// 버튼 사라짐
			
			
			/* 인증키 입력란 보이기, 인증 취소버튼 보이기  */
			tf_emailAuth.setVisible(true);
			btn_AuthCancel.setVisible(true);
			
			
			/* 6자리 난수 생성 후 입력된 이메일 계정으로 보냄 */
			authCode = (int)(Math.random() * 900000) + 100000;
			System.out.println(authCode);
			new EmailAutho(emailAccount, authCode);
			
			
			/* 타이머 초기화 후 보이기 */
			label_timer.setVisible(true);
			timerStart = true;
			min = 3;
			sec = 0;
			
			}
		

		/* ******************************************************************* */
			
		// 닉네임 중복검사 버튼 이벤트

		else if (e.getSource() == btn_nickname_check) {

			//TODO 닉네임 잠금
			if(nicknameCheck) {
			tf_nickname.setEnabled(false);
			btn_nickname_check.setVisible(false);
			label_NicknameChkLabel.setText("입력한 닉네임을 사용합니다.");
			}else {
				label_NicknameChkLabel.setText("[오류!]" + label_NicknameChkLabel.getText());
			}
			
		}

		/* ******************************************************************* */
		
		// 이메일 인증 버튼 이벤트
		
		if (e.getSource() == tf_emailAuth) {
			
			
			/* 이메일 인증키 입력란이 빈칸이 아니고, 코드가 일치하면  */
			if(tf_emailAuth.getText().length() != 0 && tf_emailAuth.getText().equals(authCode + "")){
				
				/* 인증키 입력란 비활성화, 타이머 정지&사라짐, 인증 성공 true */
				tf_emailAuth.setEnabled(false);
				tf_emailAuth.setFont(new Font("MD개성체", Font.BOLD, 13));
				tf_emailAuth.setText("인증 성공!");
				AuthSuccess = true;
				timerStart = false;
				label_timer.setVisible(false);
				tf_pwd.requestFocus();
				
				
				/* 인증 실패  */
			}else {
				tf_pwd.requestFocus();
				tf_emailAuth.setForeground(new Color(250, 100, 100));
				tf_emailAuth.setFont(new Font("MD개성체", Font.BOLD, 13));
				tf_emailAuth.setText("   error!");
			}
		
		} 
		
		
		
		/* ******************************************************************* */
		
		// 인증 취소 버튼 이벤트
		
		if (e.getSource() == btn_AuthCancel) {
			
			AuthSuccess = false;
			
			/* 인증 입력창 초기화 */
			tf_emailAuth.setForeground(new Color(170, 170, 170));
			tf_emailAuth.setText("입력 후 엔터");
			tf_emailAuth.setEnabled(true);
			
			/* 타이머 작동 중지, 인증취소, 인증 입력창, 타이머 숨기기  */
			timerStart = false;
			btn_AuthCancel.setVisible(false);
			tf_emailAuth.setVisible(false);
			tf_emailAuth.setVisible(false);
			label_timer.setVisible(false);
			
			/* 이메일 입력란 활성화,  인증 버튼 보이기  */
			tf_emailAccount.setEnabled(true); 
			comboBox_email.setEnabled(true); 
			btn_EmailAuth.setVisible(true);	  
			
		}
			
		
	} // actionPerformed();
	
	
	
	/*************************************************************************************************************/

	
	
	@Override
	public void run() {
		try {
			
			while(!threadStop) {
				
				/* ******************************************************************* */
				
				// 인증시간 타이머
				
				if(sec > 0 && timerStart) {
					
				sec--;
				
				/* 초가 0이되면 분 -1하고 59초로 만듬, 시간초과시의 동작은 구현하지 않았음 */
				}else if(sec == 0) {
					
					if(min >= 1) {
						min--;
						sec = 59;
					}else if (min == 0){
						timerStart = false;
						label_timer.setText("시간초과");
						threadStop = true;
					}
				}
				label_timer.setText("남은 시간 " + min + " : " + new DecimalFormat("00").format(sec));
				
				
				/* ******************************************************************* */
				
				// 비밀번호 체크

				String pwd = new String(tf_pwd.getPassword());
				String pwd_check = new String(tf_pwd_check.getPassword());

				/* 비밀번호와 비번확인이 모두 입력되었을 때, */
				if (pwd.length() != 0 && pwd_check.length() != 0) {

					/* 일치여부 라벨 출력 */
					label_pwdChkLabel.setVisible(true);

					if (pwd.equals(pwd_check)) {
						label_pwdChkLabel.setForeground(new Color(50, 200, 50));
						label_pwdChkLabel.setText("비밀번호가 일치합니다.");
					} else {
						label_pwdChkLabel.setForeground(new Color(200, 50, 50));
						label_pwdChkLabel.setText("비밀번호가 일치하지 않습니다.");
					}
				}
				
				/* ******************************************************************* */
				
				/* 스레드는 매 1초마다 반복된다.*/
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} // run();
	
	
	/*************************************************************************************************************/



}