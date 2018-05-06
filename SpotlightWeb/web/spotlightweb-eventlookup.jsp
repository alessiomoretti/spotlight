<%@ page import="it.uniroma2.ispw.spotlight.users.User" %>
<%@ page import="it.uniroma2.ispw.spotlight.Constants" %>
<%@ page import="it.uniroma2.ispw.spotlight.entities.Event" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="spotlightweb.EventLookupBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="loginBean" scope="session" class="spotlightweb.LoginBean"/>
<jsp:useBean id="eventLookupBean" scope="session" class="spotlightweb.EventLookupBean" />
<jsp:setProperty name="eventLookupBean" property="*"/>

<%
    // setting event lookup bean current user
    eventLookupBean.setCurrentUser(loginBean.getCurrentUser());

    ArrayList<Event> events = null;

    if (request.getParameter("search") != null) {
        // retrieving events

        if (request.getParameter("currentUserEventsCheck") != null)
            events = eventLookupBean.searchUserEvents();
        else
            events = eventLookupBean.searchEvents();

        if (events == null) {
            // error message
            %>
            <div class="modal" id="errorModal" tabindex="-1" role="dialog">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Error</h5>
                        </div>
                        <div class="modal-body">
                            <p><jsp:getProperty name="eventLookupBean" property="errorMessage"/></p>
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

<!DOCTYPE html>
<html>
<head>
    <title>Spotlight - WebApp - Events Lookup</title>


    <link rel="stylesheet" href="css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.0-alpha14/css/tempusdominus-bootstrap-4.min.css" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" type="text/css">

</head>
<body>

<ul class="nav nav-tabs" style="padding-top: 5pt;padding-left: 2pt;">
    <li class="nav-item">
        <a class="nav-link active" href="#">Events Lookup</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="#">Event Manager</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="#">Rooms Lookup</a>
    </li>
</ul>

<div class="container" style="padding-top: 20pt">

    <form id="eventSearchForm" action="spotlightweb-eventlookup.jsp">
        <div class="row">
            <div class="col-8">
                <input type="text" id="eventNameSearchText" name="eventNameSearchText" class="form-control" placeholder="Type event name" aria-label="Type event name" aria-describedby="basic-addon2" required="required">
            </div>
            <div class="col-2">
                <div class="input-group date" id="datetimepicker" data-target-input="nearest">
                    <input type="text" id="eventDateSearchText" name="eventDateSearchText" class="form-control datetimepicker-input" data-target="#datetimepicker" required="required"/>
                    <div class="input-group-append" data-target="#datetimepicker" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-calendar"></i></div>
                    </div>
                </div>
            </div>
            <div class="col-2">
                <button class="btn btn-secondary" name="search" id="search" value="search" type="submit">SEARCH EVENT</button>
            </div>
        </div>
        <%
            if (loginBean.getCurrentUser().getRole() >= Constants.TEACHER_ROLE) {
        %>
        <div class="row" style="padding-top: 5pt; padding-left: 15pt">
            <div class="col-12">
                <input class="form-check-input" type="checkbox" value="" id="currentUserEventsCheck" name="currentUserEventsCheck" onclick="handleCurrentUserCB(this)">
                <label class="form-check-label" for="currentUserEventsCheck">
                    Only current user events
                </label>
            </div>
        </div>
        <%
            }
        %>
    </form>

</div>

<div class="container" style="padding-top: 20pt">
    <div class="row">
        <div class="col-8">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th scope="col">Event name</th>
                    <th scope="col">Referral</th>
                    <th scope="col">Start date</th>
                    <th scope="col">End date</th>
                    <th scope="col"></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (events != null) {
                        for (Event event : events) {
                            %>
                                <tr>
                                    <td><% out.print(event.getEventName()); %></td>
                                    <td><% out.print(event.getReferralName()); %></td>
                                    <td><% out.print(event.getStartDateTime().toString());%></td>
                                    <td><% out.print(event.getEndDateTime().toString()); %></td>
                                    <td><i class="fa fa-info-circle" id="<% out.print(event.getEventID()); %>" onclick="populateReservedRooms(id)"></i></td>
                                </tr>
                            <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
        <div class="col-4">
            <table class="table table-sm table-striped" id="reservationsTable">
                <h6 id="selectedEvent" >No event selected</h6>
                <thead>
                <tr>
                    <th scope="col">Room</th>
                    <th scope="col">Start time</th>
                    <th scope="col">End time</th>
                </tr>
                </thead>
                <tbody>
                <!--
                <tr>
                    <td>Mark</td>
                    <td>Otto</td>
                    <td>@mdo</td>
                </tr>
                -->
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
    var reservationsJSON = <% if (events != null) out.print(EventLookupBean.getEventRoomsJSON(events));
                              else out.print("{}");
                           %>;
</script>
<script>
    $(function () {
        $('#datetimepicker').datetimepicker({
            format: 'L'
        });
    });

    function handleCurrentUserCB(cb) {
        if (cb.checked) {
            $('#eventNameSearchText').attr('disabled', 'disabled');
            $('#eventDateSearchText').attr('disabled', 'disabled');
        } else {
            $('#eventNameSearchText').removeAttr('disabled');
            $('#eventDateSearchText').removeAttr('disabled');
        }

    }

    function populateReservedRooms(eventID) {
        $('#reservationsTable tbody').html("")

        // populating selected event name
        var eventName = eventID.split("-")[0];
        $('#selectedEvent').html(eventName);

        // populating table
        reservationsJSON[eventID].forEach(function(reservation) {
            var tr = "<tr><td>"+reservation["roomName"]+"</td><td>"+reservation["reservationStart"]+"</td><td>"+reservation["reservationStart"]+"</td></tr>";
            $('#reservationsTable tbody').append(tr);
        })
    }

</script>

</body>
</html>
