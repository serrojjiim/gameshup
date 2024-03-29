package aiss.controller;

import aiss.model.resource.GoogleDriveResource;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleDriveFileDeleteController extends HttpServlet {

    private static final Logger log = Logger.getLogger(GoogleDriveFileDeleteController.class.getName());

    @Override

    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        if (id != null && !"".equals(id)) {
            String accessToken = (String) req.getSession().getAttribute("GoogleDrive-token");
            if (accessToken != null && !"".equals(accessToken)) {
                GoogleDriveResource gdResource = new GoogleDriveResource(accessToken);
                gdResource.deleteFile(id);
                log.info("File with id '" + id + "' deleted!");
//                req.getSession().setAttribute("vengoLol", true);
                if(req.getParameter("vengoLolP")!=null) {
                    req.getSession().setAttribute("vengoLol", true);
                    }else {
                        req.getSession().setAttribute("vengoLol", false);

                    }
                req.getRequestDispatcher("/googleDriveFileList").forward(req, resp);
            } else {
                log.info("Trying to access Google Drive without an access token, redirecting to OAuth servlet");
                req.getRequestDispatcher("/AuthController/GoogleDrive").forward(req, resp);
            }
        } else {
            log.warning("Invalid id for delete!");
            req.getRequestDispatcher("/googleDriveFileList").forward(req, resp);
        }
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
      String id = req.getParameter("id");
      if (id != null && !"".equals(id)) {
          String accessToken = (String) req.getSession().getAttribute("GoogleDrive-token");
          if (accessToken != null && !"".equals(accessToken)) {
              GoogleDriveResource gdResource = new GoogleDriveResource(accessToken);
              gdResource.deleteFile(id);
              log.info("File with id '" + id + "' deleted!");
//              req.getSession().setAttribute("vengoLol", true);
              if(req.getParameter("vengoLolP")!=null) {
                  req.getSession().setAttribute("vengoLol", true);
                  }else {
                      req.getSession().setAttribute("vengoLol", false);

                  }
              req.getRequestDispatcher("/googleDriveFileList").forward(req, resp);
          } else {
              log.info("Trying to access Google Drive without an access token, redirecting to OAuth servlet");
              req.getRequestDispatcher("/AuthController/GoogleDrive").forward(req, resp);
          }
      } else {
          log.warning("Invalid id for delete!");
          req.getRequestDispatcher("/googleDriveFileList").forward(req, resp);
      }
 }
  
}
