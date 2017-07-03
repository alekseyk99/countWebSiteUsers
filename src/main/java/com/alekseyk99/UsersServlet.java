package com.alekseyk99;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * "/users" servlet
 *
 */
@WebServlet(urlPatterns={"/users"})
public class UsersServlet extends HttpServlet {

		private static final long serialVersionUID = 2855492151632938252L;
	    private static final Logger logger = LoggerFactory.getLogger(UsersServlet.class.getCanonicalName());

		/**
		 * handles get requests
		 */
		@Override
	    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			
			long time=System.currentTimeMillis();
			HttpSession session = request.getSession();
			logger.info("Get /users : time={} session={} lastAccessedTime={}",time,session.getId(),(session.isNew())?0:session.getLastAccessedTime());
			
			ServletContext context = request.getServletContext();
			CountUsersCustomService customService = (CountUsersCustomService) context.getAttribute("customService");
			CountUsersNativeService nativeService = (CountUsersNativeService) context.getAttribute("nativeService");
			
			customService.processUserTime(session.getId(), time);
			nativeService.processUserTime(session.getId(), time, (session.isNew())?0:session.getLastAccessedTime());
			
		    response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
	    	writer.print("Hello World! <br> session=");
	    	writer.print(session.getId());
	    	writer.print("<br> number users from customService= ");
	    	writer.print(customService.countUsers(time));
	    	writer.print("<br> number users from nativeService= ");
	    	writer.print(nativeService.countUsers(time));
			
		}

}
