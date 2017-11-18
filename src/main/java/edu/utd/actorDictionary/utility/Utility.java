package edu.utd.actorDictionary.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utd.actorDictionary.config.GlobalProperties;

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
	 * Creates Stronger password to be stored in database
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

}
