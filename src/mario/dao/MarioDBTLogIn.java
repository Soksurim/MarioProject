package mario.dao;

import java.awt.Container;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mario.dto.MarioDTO;

public class MarioDBTLogIn extends JFrame implements ActionListener {
   
   //역할 : 로그인화면(게임로그인 화면과 별도)에서 받은 id와 pw는 확인버튼을 누르는 순간
   //      enterTable()메소드를 객체에 담겨 MarioDBTable에 넘겨줍니다(관리자는 admin/1234, 일반사용자는 사용자의id/pw)
   //      관리자의 경우, MarioDBTable.selectArticle()로, 
   //      일반 사용자의 경우new MarioDBTable(attempt).event()로 넘어가 각각 필요한 테이블이 출력됩니다.
   //      코드가 너무 복잡하고 지저분하며 일반사용자의 경우 DB의 select문 사용하지 않은 상태이므로  혹시 수정이 필요하면 알려주세요 -조윤주
   
   
   //1.컴포넌트 필드 선언
   private JLabel label_id, label_pwd, label_pwdCheck, label_securenotice;
   private JComboBox<String> comboBox_email;
   private JTextField tf_emailAccount;
   private JPasswordField tf_pwd;
   private JButton okBtn, cancleBtn;

   
   public MarioDBTLogIn() {
      
      super("회원확인");
      
      //3.컴포넌트 생성
      label_securenotice = new JLabel("<html><body>  [안내]보안을 위해 다시 한번 <br><br>아이디와 비밀번호를 입력해주세요</body></html>");
      label_securenotice.setFont(new Font("MD개성체", Font.BOLD, 11));
      label_id = new JLabel("Email 계정 ");
      label_id.setFont(new Font("MD개성체", Font.BOLD, 15));
      comboBox_email = new JComboBox<String>(new String[]{"naver.com", "gmail.com"});
      label_pwd = new JLabel("비밀번호   ");
      label_pwd.setFont(new Font("MD개성체", Font.BOLD, 15));
      
      
      label_pwdCheck = new JLabel();  
      label_pwdCheck.setFont(new Font("MD개성체", Font.BOLD, 9));  
      label_pwdCheck.setVisible(false); 
      tf_emailAccount = new JTextField(4);
      tf_pwd = new JPasswordField(8);
      okBtn = new JButton("확인");
      cancleBtn = new JButton("취소");

      
      label_securenotice.setBounds(50, 10, 250, 40);
      
      Panel p1 = new Panel();
      p1.add(label_id);
      p1.add(tf_emailAccount);
      p1.add(comboBox_email);
      p1.setBounds(12, 100, 260, 30);
      
      Panel p2 = new Panel();
      p2.add(label_pwd);
      p2.add(tf_pwd);
      p2.setBounds(16, 150, 200, 30);
      
      label_pwdCheck.setBounds(30, 180, 200, 40); 
      
      Panel p3 = new Panel();
      p3.add(okBtn);
      p3.add(cancleBtn);
      p3.setBounds(40, 250, 200, 40);
      
      
      Container container = this.getContentPane();
      container.add(label_securenotice);
      container.add(label_pwdCheck); 
      container.add(p1);
      container.add(p2);
      container.add(p3);
      
      //2.프레임 생성
      setUndecorated(true);
      setLayout(null);
      setBounds(700,300, 300, 300);
      setVisible(true);
      setResizable(false);
      
      
      }//MarioDBTLogIn()
      

   //4.이벤트메소드
   public void event() {
      okBtn.addActionListener(this);
      cancleBtn.addActionListener(this);
      
      
      //★★★★★★★★수정, 아이디비번 불일치 경고 없애기
      label_pwdCheck.addFocusListener( new FocusListener() {
         
         @Override
         public void focusLost(FocusEvent e) {
                   
         }
         
         @Override
         public void focusGained(FocusEvent e) {
            
            label_pwdCheck.setText("");     
         }
      });
   }
   
   
   //5.버튼 작동
   @Override
   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == okBtn) { //확인버튼을 눌렀을때 입력한 id/pw와 DB에 들어있는 id/pw가 같으면 enterTable(MarioDBTable)실행
                                   //직접 new MarioDBTable()하지 않은 이유는 객체도 같이 넘겨주기 위해(관리자와 일반사용자 구분용)
       
    
       if(tf_emailAccount.getText().equals("admin") && new String(tf_pwd.getPassword()).equals("1234")) {
          
          enterTable("admin","1234"); 
       }else {
           MarioDAO dao = MarioDAO.getInstance();
           List<MarioDTO> dtoList =  dao.getMarioList();
           for(MarioDTO dto : dtoList) {
              String[] checkID = dto.getClientAccount().split("@");
              String id = checkID[0];
                if(tf_emailAccount.getText().equals(id) && new String(tf_pwd.getPassword()).equals(dto.getPassword())) {
                   
                   enterTable(tf_emailAccount.getText(),new String(tf_pwd.getPassword())); 
                   
                }else {
                   
                   label_pwdCheck.setVisible(true);
                   label_pwdCheck.setText("아이디와 비밀번호가 일치하지 않습니다");
                   
                }
           }//for문
          
       } 
       
      }else if(e.getSource() == cancleBtn) {
         dispose();
      }
      
   }//actionPerformed
  
   //6.확인버튼 작동시 DBTable클래스 호출
   public void enterTable(String id, String pw) {
      
      MarioDTO attempt = new MarioDTO();
      attempt.setClientAccount(id);
      attempt.setPassword(pw);
      
      new MarioDBTable(attempt).event();
      if(id.equals("admin") && pw.equals("1234")) {
         MarioDBTable.selectArticle();
      }else {
         new MarioDBTable(attempt).event();
      }
   }//enterTable
   
   
   //7.메인메소드
   public static void main(String[] args) {
      new MarioDBTLogIn().event();
      
   }


}

