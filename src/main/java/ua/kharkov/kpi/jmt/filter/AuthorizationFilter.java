package ua.kharkov.kpi.jmt.filter;

import ua.kharkov.kpi.jmt.model.User;
import ua.kharkov.kpi.jmt.repository.UserDAO;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@WebFilter(filterName = "AuthorizationFilter", servletNames = {"PersonalPageServlet", "StatServlet"}, urlPatterns = {"/play.jsp"})
public class AuthorizationFilter extends HttpFilter {

    @Inject
    private UserDAO userDAO;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(true); // obtain session

        if(session.getAttribute("user") == null) {
            session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            if (req.getParameter("email") != null) { // if it's new session or user didn't input info
                String email = req.getParameter("email");
                String pass = req.getParameter("password");
                User user = userDAO.findUserByEmail(email);
                if (user == null || !Objects.equals(user.getPassword(), pass)) {
                    req.setAttribute("msg", "User not found");
                    req.setAttribute("redirect", "personal_page");
                    req.getRequestDispatcher("./massage.jsp").forward(req, res);
                    return;
                }

                session.setAttribute("user", user);
            } else {
                res.sendRedirect("./authorization.html");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
