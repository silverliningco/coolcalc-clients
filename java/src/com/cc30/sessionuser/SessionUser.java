package com.cc30.sessionuser;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SessionUser
 */
@WebServlet("/user/staging/SessionUser")
public class SessionUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SessionUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Implement your own code here, sample output is provided below.
		 * If user is logged in, respond with known account nr and individual user id.
		 * If user is not logged in, generate a random John-or-Jane-Doe id for the duration of the session.
		 * When a not logged in user becomes known, add a direct API call at the end of your login flow
		 * to update the identifying information for any work that the not logged in user may have done.
		 */
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	    response.getWriter().write("{ \"dealerReference\": \"nice-contractor\", \"userReference\": \"nice-user\", \"isAdmin\": true }");
	}

}
