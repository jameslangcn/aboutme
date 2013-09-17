package com.turbo_license.aboutme;

/*********************************************
 * Response to ajax request
 * Created by huan zhang, 09/04/2009
 * input:  request parameter: int currentPage, String item_id
 * output: ratings/comments as xml format
 * 
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Blob;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.turbo_license.dao.LicenseDAO;
import com.turbo_license.dao.LicenseInUseDAO;
import com.turbo_license.dao.UserDAO;
import com.turbo_license.database.ConnFactory;
import com.turbo_license.database.SqlConn;
import com.turbo_license.utilities.Base64;
import com.turbo_license.utilities.Validator;

public class CommonAjax extends MasterServlet {
	
	Logger logger = Logger.getLogger(CommonAjax.class);
	static final int DEFAULT_LICENSE_ACTIVE_TIME = 30000;

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.info("this is init");
		super.init(config);
		ServletContext context = config.getServletContext();
		loadProps(context);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.init();
		String func = request.getParameter("function");
		Element elResult = new Element("result");
		Method mthd;
		try {
			mthd = this.getClass().getDeclaredMethod(func, Class.forName("javax.servlet.http.HttpServletRequest"), elResult.getClass());
			elResult = (Element) mthd.invoke(this, request, elResult);
		} catch (Exception e) {
			logger.error("WebposAjax invoke error: " + e, e);
		}

		// OUTPUT TO CLIENT
		try {
			String msg = new XMLOutputter().outputString(new Document(elResult));
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			OutputStream out = response.getOutputStream();
			out.write(msg.getBytes());
			out.close();
		} catch (Exception e) {
			logger.error("Failed to update DAO via AJAX " + e.getMessage(), e);
		} // end for
	} // end if

	private Element signup(HttpServletRequest request, Element elResult) {
		Element elResponse = new Element ("response");
		String username = request.getParameter("username");
		String password1 = request.getParameter("password1");
		String password2 = request.getParameter("password2");
		String email = request.getParameter("email");
		String fName = request.getParameter("f_name");
		String lName = request.getParameter("l_name");
		
		if (username == null || "".equals(username)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Username shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (password1 == null || "".equals(password1)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Password shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (password2 == null || "".equals(password2)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "The repeated password shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (!password2.equals(password1)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "asswords are not match.");
			elResult.addContent(elResponse);
			return elResult;
		}
		if (email == null || "".equals(email)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Email shouldn't be blank.");
			elResult.addContent(elResponse);
			return elResult;
		}
		Validator validator = new Validator(Validator.EMAIL_PATTERN);
		if(!validator.validate(email)) {
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "Email's format is not correct.");
			elResult.addContent(elResponse);
			return elResult;
		}
		SqlConn con = null;
		String sql = "";
		ResultSet rs = null;
		try {
			con = ConnFactory.getConnection("license");
			sql = "select count(*) as count from user where username = '" + username + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				if (rs.getInt("count") > 0) {
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "The username exists.");
					elResult.addContent(elResponse);
					return elResult;
				}
			}
			rs = null;
			sql = "select count(*) as count from user where email = '" + email + "'";
			rs = con.query(sql);
			if (rs != null && rs.next()) {
				if (rs.getInt("count") > 0) {
					elResponse.setAttribute("success", "0");
					elResponse.setAttribute("errmsg", "The email address exists.");
					elResult.addContent(elResponse);
					return elResult;
				}
			}
			UserDAO userDao = new UserDAO();
			userDao.setField("username", username);
			userDao.setField("password", password1);
			userDao.setField("email", email);
			userDao.setField("first_name", fName);
			userDao.setField("last_name", lName);
			userDao.save();
			elResponse.setAttribute("success", "1");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			elResponse.setAttribute("success", "0");
			elResponse.setAttribute("errmsg", "inserting into db failed: " + e.getMessage());
		} finally {
			con.close();
		}
		elResult.addContent(elResponse);
		return elResult;
	}
	
	

}
