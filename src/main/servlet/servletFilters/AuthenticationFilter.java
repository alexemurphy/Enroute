package main.servlet.servletFilters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

	private ServletContext context;


	public void init(FilterConfig fConfig) {
		this.context = fConfig.getServletContext();
		this.context.log("AuthenticationFilter initialized");
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		this.context.log("Requested Resource::" + uri);

		HttpSession session = req.getSession(false);
		this.context.log(uri);
		if (session == null && !(uri.endsWith("html") || uri.endsWith("LoginServlet")) && !uri.endsWith("index.jsp")) {
			this.context.log("Unauthorized access request");
			//System.out.println("Unauthorized access request");
			res.sendRedirect("index.jsp");
		} else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}


	}


	public void destroy() {
		//close any resources here
	}
}