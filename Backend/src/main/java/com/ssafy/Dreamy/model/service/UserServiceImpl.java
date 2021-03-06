package com.ssafy.Dreamy.model.service;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ssafy.Dreamy.model.UserDto;
import com.ssafy.Dreamy.model.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public boolean login(String email, String password) throws Exception {
		if (email == null || password == null)
			return false;

		Map<String, String> map = new HashMap<>();
		map.put("email", email);
		map.put("password", password);
		int res=sqlSession.getMapper(UserMapper.class).login(map);
		if(res==1)
			return true;
		else
			return false; 
	}
	
	@Override
	public UserDto setUser(String email) throws Exception {
		return sqlSession.getMapper(UserMapper.class).setUser(email);
	}

	@Override
	public String getLoginType(String email) throws Exception {
		return sqlSession.getMapper(UserMapper.class).getLoginType(email);
	}
	
	@Override
	public int signup(UserDto userDto) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("email", userDto.getEmail());
		map.put("name", userDto.getName());
		map.put("password", userDto.getPassword());
		map.put("phone", userDto.getPhone());
		map.put("profileUrl", userDto.getProfileUrl());
		if (userDto.getLoginType()==null)
			map.put("loginType", "default");
		else
			map.put("loginType", userDto.getLoginType());
		return sqlSession.getMapper(UserMapper.class).signup(map);
	}
	
	@Override
	public int getEmail(String email) throws Exception {
		int ret = sqlSession.getMapper(UserMapper.class).getEmail(email);
		return ret;
	}
	
	@Override
	public int getName(String name) throws Exception {
		int ret = sqlSession.getMapper(UserMapper.class).getName(name);
		return ret;
	}

	@Override
	public int confirm(int uid, String password) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("uid", uid);
		map.put("password", password);
		return sqlSession.getMapper(UserMapper.class).confirm(map);
	}
	
	@Override
	public int update(UserDto userDto) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("uid", userDto.getUid());
		map.put("password", userDto.getPassword());
		map.put("phone", userDto.getPhone());
		return sqlSession.getMapper(UserMapper.class).update(map);
	}
	
	@Override
	public int delete(int uid) throws Exception {
		return sqlSession.getMapper(UserMapper.class).delete(uid);
	}

	@Override
	public int certification(String email, String name) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("email", email);
		map.put("name", name);
		int result = sqlSession.getMapper(UserMapper.class).certification(map);
		
		return result;
	}

	@Override
	public int updatePassword(String email, String password) throws SQLException {
		Map<String, String> map = new HashMap<>();
		map.put("email", email);
		map.put("password", password);
		return sqlSession.getMapper(UserMapper.class).updatePassword(map);
	}

	@Override
	public UserDto userInfo(int uid) throws Exception {
		return sqlSession.getMapper(UserMapper.class).userInfo(uid);
	}
	
	@Override
	public int updateProfile(int uid, String profileUrl) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("uid", uid);
		map.put("profileUrl", profileUrl);
		return sqlSession.getMapper(UserMapper.class).updateProfile(map);
	}

	@Override
	@Async
	public int sendEmail(String email, String newPassword) throws Exception {
		int result = 0;
		String host = "smtp.gmail.com"; // ???????????? ?????? ????????? ??????, gmail?????? gmail ?????? 
		String user = "dreamya306@gmail.com"; 
		// ???????????? 
		String password = "ssafyA306!";      
		
		// SMTP ?????? ????????? ??????. 
		Properties props = new Properties(); 
		props.put("mail.smtp.host", host); 
		props.put("mail.smtp.port", 465); 
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { 
			protected PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(user, password); 
			} 
		}); 
		
		try { 
			MimeMessage message = new MimeMessage(session); 
			message.setFrom(new InternetAddress(user)); 
			
			// ????????? ?????? ??????
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email)); 
			// ?????? ?????? 
			message.setSubject("[Dreamy] ?????????????????? ??????."); 
			// ?????? ?????? 

			message.setContent("<div>\r\n"
					+ "		<section style=\"padding:50px 0;background-image: url('https://dreamybucket.s3.ap-northeast-2.amazonaws.com/background.png'); background-repeat:no-repeat; background-size: 100% 100%;\">\r\n"
					+ "			<div style=\"width:540px;margin:0 auto;padding:30px;text-align:center;background-color:#fff;box-shadow:1px 2px 2px rgba(0,0,0,.1);border-radius:3px;\">\r\n"
					+ "				<h3 style=\"font-style:italic\">DREAMY</h3>\r\n"
					+ "				<hr>\r\n"
					+ "				<p style=\"margin:73px 0 50px;text-align:center;color:#757575;font-size:20px;letter-spacing:-1px\">\r\n"
					+ "				<strong style=\"display:block;margin-bottom:22px;color:#212121;font-size:28px;font-weight:normal\">?????? ???????????? ?????? ?????? ??????</strong>\r\n"
					+ "				???????????? ?????? ??????????????? ????????? ????????????.\r\n"
					+ "				<br>\r\n"
					+ "				<br>\r\n"
					+ "				<strong style=\"color:#0100FF;font-weight:normal\">?????? ???????????? :" + newPassword + "</strong>\r\n"
					+ "				<br>\r\n"
					+ "				<hr>\r\n"
					+ "				<br>\r\n"
					+ "				<div style=\"font-size:12px; text-align:left;\">\r\n"
					+ "					- ?????? ??????????????? ????????? ?????? ???, ????????? ??????????????? ??????????????????.<br>\r\n"
					+ "					- ??????????????? DREAMY ???????????? ????????? > ??????????????? > ???????????? ?????? ?????? ???????????? ??? ????????????.<br>\r\n"
					+ "					- ????????? ????????? ????????? ?????? ??????????????? ??????????????? ??????????????? ?????? ????????????.<br>\r\n"
					+ "				</div>\r\n"
					+ "				<br>		\r\n"
					+ "				<a href=\"http://i4a306.p.ssafy.io\" style=\"display:inline-block; margin-top:30px; padding: 0 30px;border: 1px solid blue; border-radius:5px;color:#6D6CFF; text-decoration:none\">DREAMY ????????????</a>	\r\n"
					+ "				</p>\r\n"
					+ "			</div>\r\n"
					+ "	\r\n"
					+ "		</section>\r\n"
					+ "	</div>"
								,"text/html;charset=euckr");
			// send the message 
			Transport.send(message); 
			
		} catch (MessagingException e) { 
			result = -1;
			e.printStackTrace(); 
		}
		
		return result;
	}

	@Override
	public String createPassword() throws Exception {
		String password = "";
		// ?????? ????????? SHA-256 ??????
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String msg = format.format(date);
		
		MessageDigest md;
		try {
			// SHA-256?????? ??????
			md = MessageDigest.getInstance("SHA-256");
			
			md.update(msg.getBytes());
			// ???????????? ??????????????? ??????
			StringBuilder sb = new StringBuilder();
			
			for (byte b : md.digest())
				sb.append(String.format("%02x", b));
			
			password = sb.substring(sb.length() - 8, sb.length());
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}	
		
		return password;
	}
	
}
