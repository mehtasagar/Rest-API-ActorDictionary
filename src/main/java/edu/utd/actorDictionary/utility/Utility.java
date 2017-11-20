package edu.utd.actorDictionary.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utd.actorDictionary.config.GlobalProperties;
import edu.utd.actorDictionary.dto.RoleDTO;
import edu.utd.actorDictionary.dto.ValidDate;

@Service
public class Utility {

	private GlobalProperties global;

	@Autowired
	public void setGlobal(GlobalProperties global) {
		this.global = global;
	}

	/**
	 * This function validates the request
	 * 
	 * @param secretKey
	 * @return
	 */
	public boolean validate(String secretKey) {
		if (secretKey.equals(global.getSecretKey())) {
			return true;
		}
		return false;
	}

	/**
	 * Creates Stronger password to be stored in database by adding Md5 hased salt is added to the received MD5hased password and then reversing
	 * @param hashedPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String modifyPassword(String hashedPassword) throws NoSuchAlgorithmException {
		String salt = global.getSalt();

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(salt.getBytes());
		byte[] digest = md.digest();
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();

		 myHash = myHash + hashedPassword;
		/*char[] temp=myHash.toCharArray();
		Arrays.sort(temp);*/
	//	myHash= temp.toString();
		StringBuilder sb = new StringBuilder(myHash);

		return sb.reverse().toString();

	}

	/**
	 * Validates dates to be of format yy/MM/dd and that enddate if not null is > start date.
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	public ValidDate validateDate(RoleDTO input) throws ParseException {
		SimpleDateFormat sdf= new SimpleDateFormat("yy/MM/dd");
		ValidDate date= new ValidDate();
		if((input.getStart()==null || input.getStart().isEmpty()) && (input.getEnd()==null || input.getEnd().isEmpty())  ){
			date.setValid(true);
			return date;
		}
		if(input.getStart()!=null){
			//date.setStartDate(input.getStart().replace("/", ""));
			date.setStartDate(input.getStart());
			date.setValid(true);
			if(input.getEnd()!=null){
				Date start = sdf.parse(input.getStart());
				Date end = sdf.parse(input.getEnd());
				if(end.getTime()-start.getTime()>=0){
					//date.setEndDate(input.getEnd().replace("/", ""));
					date.setEndDate(input.getEnd());
				}else{
					date.setValid(false);
				}
			}
		}
		return date;
	}

}
