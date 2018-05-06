<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- setting the login bean with scope the request -->
<jsp:useBean id="loginBean" scope="session" class="spotlightweb.LoginBean"/>
<jsp:setProperty name="loginBean" property="*" />

<!DOCTYPE html>
<html>
  <head>
    <title>Spotlight - Login</title>

    <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="css/login.css" type="text/css">
  </head>
  <body>
  <!-- performing login validation -->
  <%
    if (request.getParameter("login") != null) {
      // authenticating
      if (loginBean.authenticate()) {
          %>
          <jsp:forward page="spotlightweb-eventlookup.jsp"/>
          <%
      } else {
          %>
          <div class="modal" id="errorModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title">Login Error</h5>
                </div>
                <div class="modal-body">
                  <p><strong>An error occured during login.</strong><br>Please check your credentials and try again.</p>
                </div>
                <div class="modal-footer">
                  <button type="button" id="modalClose" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
              </div>
            </div>
          </div>
          <%
      }
  }
  %>

  <div class="wrapper">
    <form class="form-signin" action="index.jsp">
      <h2 class="form-signin-heading">Spotlight Login</h2>
      <input type="text" class="form-control" name="username" placeholder="Email Address" required="" autofocus="" />
      <input type="password" class="form-control" id="password" name="password" placeholder="Password" required=""/>

      <button class="btn btn-lg btn-primary btn-block" name="login" id="login" value="login" type="submit">Login</button>
    </form>
  </div>

  <script src="js/bootstrap.min.js"></script>
  <script   src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
  <script>
    $('#errorModal').show();
    $('#modalClose').click( function() {
        $('#errorModal').hide();
    })
  </script>
  </body>
</html>
