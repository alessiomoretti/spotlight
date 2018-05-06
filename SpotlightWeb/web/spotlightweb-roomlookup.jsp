<%@ page import="it.uniroma2.ispw.spotlight.users.User" %>
<%@ page import="it.uniroma2.ispw.spotlight.Constants" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="spotlightweb.EventLookupBean" %>
<%@ page import="it.uniroma2.ispw.spotlight.entities.Room.Room" %>
<%@ page import="spotlightweb.RoomLookupBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="loginBean" scope="session" class="spotlightweb.LoginBean"/>
<jsp:useBean id="roomLookupBean" scope="session" class="spotlightweb.RoomLookupBean"/>
<%
    if (loginBean.getCurrentUser() == null) {
        %>
        <jsp:forward page="index.jsp"/>
        <%
    }
%>
<%
    // setting room lookup bean current user
    roomLookupBean.setCurrentUser(loginBean.getCurrentUser());

    // retrieving rooms
    ArrayList<Room> rooms = roomLookupBean.getRooms();
    if (rooms == null) {
        // error message
        %>
        <div class="modal" id="errorModal" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Error</h5>
                    </div>
                    <div class="modal-body">
                        <p><jsp:getProperty name="roomLookupBean" property="errorMessage"/></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="modalClose" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
        <%
    }
%>


<!DOCTYPE html>
<html>
<head>
    <title>Spotlight - WebApp - Rooms Lookup</title>


    <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.0-alpha14/css/tempusdominus-bootstrap-4.min.css" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.12/css/all.css" type="text/css">
</head>
<body>

<ul class="nav nav-tabs" style="padding-top: 5pt;padding-left: 2pt;">
    <li class="nav-item">
        <a class="nav-link" href="spotlightweb-eventlookup.jsp">Events Lookup</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="spotlightweb-eventmanager.jsp">Event Manager</a>
    </li>
    <li class="nav-item">
        <a class="nav-link active" href="#">Rooms Lookup</a>
    </li>
</ul>

<div class="container" style="padding-top: 20pt">
    <div class="row">
        <div class="col-4">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th scope="col">Room</th>
                    <th scope="col">Department</th>
                    <th scope="col"></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (rooms != null) {
                        for (Room room : rooms) {
                %>
                <tr>
                    <td><% out.print(room.getRoomName()); %></td>
                    <td><% out.print(room.getRoomDepartment()); %></td>
                    <td><i class="fa fa-bookmark" id="<% out.print(room.getRoomID() + "%" + room.getRoomName()); %>" onclick="populateReservations(id)"></i></td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
        <div class="col-8">
            <table class="table table-sm table-striped" id="reservationsTable">
                <h6 id="selectedRoom" >No room selected</h6>
                <thead>
                <tr>
                    <th scope="col">Reservation</th>
                    <th scope="col">Start time</th>
                    <th scope="col">End time</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script src="js/bootstrap.min.js"></script>
<script src="js/moment.js"></script>
<script   src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.0-alpha14/js/tempusdominus-bootstrap-4.min.js"></script>
<script>
    var reservationsJSON = <% if (rooms != null) out.print(roomLookupBean.getRoomsReservationsJSON(rooms));
                              else out.print("{}");
                           %>;
</script>
<script>
    function populateReservations(ID) {
        $('#reservationsTable tbody').html("");

        // populating selected event name
        var roomID   = ID.split("%")[0];
        var roomName = ID.split("%")[1];
        $('#selectedRoom').html(roomName);

        // populating table
        reservationsJSON[roomID].forEach(function(reservation) {
            var tr = "<tr><td>"+reservation["reservation"]+"</td><td>"+reservation["reservationStart"]+"</td><td>"+reservation["reservationEnd"]+"</td></tr>";
            $('#reservationsTable tbody').append(tr);
        })
    }
</script>
</body>
</html>
