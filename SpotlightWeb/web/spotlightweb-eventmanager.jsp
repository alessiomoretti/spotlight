<%@ page import="it.uniroma2.ispw.spotlight.entities.Event" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="it.uniroma2.ispw.spotlight.Constants" %>
<%@ page import="spotlightweb.EventLookupBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="loginBean" scope="session" class="spotlightweb.LoginBean"/>
<jsp:useBean id="eventLookupBean" scope="session" class="spotlightweb.EventLookupBean" />
<jsp:useBean id="roomLookupBean" class="spotlightweb.RoomLookupBean" scope="session" />

<%
    if (loginBean.getCurrentUser() == null) {
        %>
        <jsp:forward page="index.jsp"/>
        <%
    } else {
        if (loginBean.getCurrentUser().getRole() < Constants.TEACHER_ROLE) {
            %>
            <jsp:forward page="index.jsp"/>
            <%
        }

        // adding user to request context
        request.getSession().setAttribute("currentUser", loginBean.getCurrentUser());
        // adding user to roomlookup
        roomLookupBean.setCurrentUser(loginBean.getCurrentUser());
    }
%>

<%
    ArrayList<Event> events = eventLookupBean.searchUserEvents();
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
%>

<!DOCTYPE html>
<html>
<head>
    <title>Spotlight - WebApp - Rooms Lookup</title>


    <link rel="stylesheet" href="css/bootstrap.css" type="text/css" />
    <link rel="stylesheet" href="css/eventmgmt.css" type="text/css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.0-alpha14/css/tempusdominus-bootstrap-4.min.css" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.12/css/all.css" type="text/css" />

</head>
<body>

<ul class="nav nav-tabs" style="padding-top: 5pt;padding-left: 2pt;">
    <li class="nav-item">
        <a class="nav-link" href="spotlightweb-eventlookup.jsp">Events Lookup</a>
    </li>
    <li class="nav-item">
        <a class="nav-link active" href="#">Event Manager</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="spotlightweb-roomlookup.jsp">Rooms Lookup</a>
    </li>
</ul>
<div class="container" style="padding-top: 5pt">
    <div class="row">
        <div class="col-10">
            <p>Events created by: <strong><%out.print(loginBean.getCurrentUser().getUsername());%></strong></p>
        </div>
        <div class="col-2">
            <button id="btnNewEvent" class="btn btn-primary btn-sm" style="float:right" onclick="$('#newEventModal').show();">
                NEW EVENT
                <i class="fa fa-calendar"></i>
            </button>
        </div>
    </div>
    <div class="row">
        <div class="col-12">
            <table class="table table-sm table-hover">
                <thead class="thead-dark">
                <tr>
                    <th scope="col" style="width: 25%">Event name</th>
                    <th scope="col" style="width: 20%">Referral</th>
                    <th scope="col" style="width: 20%">Mailing list</th>
                    <th scope="col" style="width: 12.5%">Start date</th>
                    <th scope="col" style="width: 12.5%">End date</th>
                    <th scope="col" style="width: 5%">Reserved rooms</th>
                    <th scope="col" style="width: 5%"></th>
                </tr>
                </thead>
                <tbody style="height: 220px; overflow-y: auto">
                <%
                    if (events != null) {
                        for (Event event : events) {
                            %>
                            <tr>
                                <td style="width: 25%"><% out.print(event.getEventName()); %></td>
                                <td style="width: 20%"><% out.print(event.getReferralName()); %></td>
                                <td style="width: 20%"><% out.print(event.getEmailDL()); %></td>
                                <td style="width: 12.5%"><% out.print(event.getStartDateTime().toString());%></td>
                                <td style="width: 12.5%"><% out.print(event.getEndDateTime().toString()); %></td>
                                <td style="width: 5%"><% out.print(String.valueOf(event.getReservedRooms().size())); %></td>
                                <td style="width: 5%"><i class="fa fa-info-circle" id="<% out.print(event.getEventID() + "%" + event.getEventName() + "%" + event.getEmailDL() + "%" + String.valueOf(event.getStartDateTime().getTime()) + "%" + String.valueOf(event.getEndDateTime().getTime())); %>" onclick="populateEventDetails(id)"></i></td>
                            </tr>
                            <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-6">
            <div class="card" style="height: 250px">
                <div class="card-body">
                    <div class="input-group input-group-sm mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text" >Event name</span>
                        </div>
                        <input type="text" id="eventNameUpdate" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
                    </div>
                    <div class="input-group input-group-sm mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text" >Event email</span>
                        </div>
                        <input type="text" id="eventMailUpdate" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <div class="label" style="font-size: small">Event start at:</div>
                            <div class="input-group date" id="datetimepicker" data-target-input="nearest">
                                <input type="text" id="startTimeUpdateText" name="eventDateSearchText" class="form-control datetimepicker-input" data-target="#datetimepicker" required="required" style="font-size: small"/>
                                <div class="input-group-append" data-target="#datetimepicker" data-toggle="datetimepicker">
                                    <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                                </div>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="label" style="font-size: small">Event end at:</div>
                            <div class="input-group date" id="datetimepicker1" data-target-input="nearest">
                                <input type="text" id="endTimeUpdateText" name="eventDateSearchText" class="form-control datetimepicker-input" data-target="#datetimepicker" required="required" style="font-size: small"/>
                                <div class="input-group-append" data-target="#datetimepicker1" data-toggle="datetimepicker">
                                    <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="padding-top: 30px">
                        <div class="col-5">
                            <button id="addReservationBtn" class="btn btn-outline-info btn-sm" onclick="$('#addReservationModal').show();">
                                ADD RESERVATION
                                <i class="fa fa-bookmark"></i>
                            </button>
                        </div>
                        <div class="col-4">
                            <button id="deleteEventBtn" class="btn btn-outline-danger btn-sm" onclick="deleteEvent(); if (deletedEvent) location.reload();">
                                DELETE EVENT
                                <i class="fa fa-trash-alt"></i>
                            </button>
                        </div>
                        <div class="col-3">
                            <button id="updateEventBtn" class="btn btn-outline-primary btn-sm" style="float:right" onclick="updateEvent(); if (updatedEvent) location.reload();">
                                UPDATE EVENT
                                <i class="fa fa-save"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-6">
            <div class="card">
                <table class="table table-sm table-hover" id="reservationsTable">
                    <thead class="thead-light">
                    <tr>
                        <th scope="col">Room</th>
                        <th scope="col">Department</th>
                        <th scope="col">Start time</th>
                        <th scope="col">End time</th>
                        <th scope="col" style="width: 5%"></th>
                    </tr>
                    </thead>
                    <tbody style="height: 200px; overflow-y: auto">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal" id="newEventModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">New event</h5>
            </div>
            <div class="modal-body">
                <div class="input-group input-group-sm mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text">@</span>
                    </div>
                    <input id="eventReferralCreate" type="text" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm" disabled value="<% out.print(loginBean.getCurrentUser().getName()); %>">
                </div>
                <div class="input-group input-group-sm mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" >Event name</span>
                    </div>
                    <input id="eventNameCreate" type="text" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
                </div>
                <div class="input-group input-group-sm mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" >Event email</span>
                    </div>
                    <input id="eventMailCreate" type="text" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
                </div>
                <div class="row">
                    <div class="col-6">
                        <div class="label" style="font-size: small">Event start at:</div>
                        <div class="input-group date" id="datetimepicker2" data-target-input="nearest">
                            <input type="text" id="startTimeCreateText" class="form-control datetimepicker-input" data-target="#datetimepicker2" required="required" style="font-size: small"/>
                            <div class="input-group-append" data-target="#datetimepicker2" data-toggle="datetimepicker">
                                <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="label" style="font-size: small">Event end at:</div>
                        <div class="input-group date" id="datetimepicker3" data-target-input="nearest">
                            <input type="text" id="endTimeCreateText"class="form-control datetimepicker-input" data-target="#datetimepicker3" required="required" style="font-size: small"/>
                            <div class="input-group-append" data-target="#datetimepicker3" data-toggle="datetimepicker">
                                <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="alert alert-danger" role="alert" style="padding-top: 10px" id="alertErrorEvent" hidden>
            </div>
            <div class="alert alert-success" role="alert" style="padding-top: 10px" id="alertSuccessEvent" hidden>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" onclick="$('#newEventModal').hide(); if (createdEvent) location.reload();">CANCEL</button>
                <button type="button" class="btn btn-primary btn-sm" onclick="createEvent()">CREATE EVENT</button>
            </div>
        </div>
    </div>
</div>


<div class="modal" id="addReservationModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">New reservation</h5>
            </div>
            <div class="modal-body">
                <div class="input-group input-group-sm mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="resCapacity">Room capacity</span>
                    </div>
                    <input type="number" id="reservationCapacity" min="0" data-bind="value:replyNumber" />
                </div>
                <div class="form-group">
                    <label for="departmentSelect">Select department</label>
                    <select class="form-control" id="departmentSelect">
                        <%
                            ArrayList<String> departments = roomLookupBean.getDepartments();
                            if (departments != null) {
                                for (String dept : departments) {
                                    %>
                                        <option><% out.print(dept); %></option>
                                    <%
                                }
                            }
                        %>
                    </select>
                </div>
                <div class="form-check" style="padding-top: 10px">
                    <input class="form-check-input" type="checkbox" value="" id="microphoneCheck">
                    <label class="form-check-label" for="microphoneCheck">
                        Microphone
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="projectorCheck">
                    <label class="form-check-label" for="projectorCheck">
                        Projector
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="whiteboardCheck">
                    <label class="form-check-label" for="whiteboardCheck">
                        Whiteboard
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="intWhiteboardCheck">
                    <label class="form-check-label" for="intWhiteboardCheck">
                        Interactive whiteboard
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="videocallCheck">
                    <label class="form-check-label" for="videocallCheck">
                        Videocall capable
                    </label>
                </div>
                <div class="row" style="padding-top: 15px">
                    <div class="col-6">
                        <div class="label" style="font-size: small">Reservation start at:</div>
                        <div class="input-group date" id="datetimepicker4" data-target-input="nearest">
                            <input type="text" id="startTimeReservationText" class="form-control datetimepicker-input" data-target="#datetimepicker4" required="required" style="font-size: small"/>
                            <div class="input-group-append" data-target="#datetimepicker4" data-toggle="datetimepicker">
                                <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="label" style="font-size: small">Reservation end at:</div>
                        <div class="input-group date" id="datetimepicker5" data-target-input="nearest">
                            <input type="text" id="endTimeReservationText"class="form-control datetimepicker-input" data-target="#datetimepicker5" required="required" style="font-size: small"/>
                            <div class="input-group-append" data-target="#datetimepicker5" data-toggle="datetimepicker">
                                <div class="input-group-text"><i class="fa fa-calendar-alt"></i></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-check" style="padding-top: 5px">
                    <input class="form-check-input" type="checkbox" value="" id="adminPrivileges">
                    <label class="form-check-label" for="adminPrivileges">
                        Administrative privileges (force room reservation)
                    </label>
                </div>
                <div class="alert alert-danger" role="alert" style="padding-top: 10px" id="alertErrorReservation" hidden>
                </div>
                <div class="alert alert-success" role="alert" style="padding-top: 10px" id="alertSuccessReservation" hidden>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" onclick="$('#addReservationModal').hide(); if (addedReservation) location.reload(); ">CANCEL</button>
                <button type="button" class="btn btn-primary btn-sm" onclick="addReservation();">ADD RESERVATION</button>
            </div>
        </div>
    </div>
</div>

<script src="js/bootstrap.min.js"></script>
<script src="js/eventmanagement.js"></script>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="js/moment.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.0-alpha14/js/tempusdominus-bootstrap-4.min.js"></script>
<script type="text/javascript">
    $.fn.datetimepicker.Constructor.Default = $.extend({}, $.fn.datetimepicker.Constructor.Default, {
        icons: {
            time: 'far fa-clock',
            date: 'far fa-calendar',
            up: 'fa fa-arrow-up',
            down: 'fa fa-arrow-down',
            previous: 'fa fa-chevron-left',
            next: 'fa fa-chevron-right',
            today: 'fa fa-calendar-check-o',
            clear: 'fa fa-trash',
            close: 'fa fa-times'
        } });
    $(function () {
        $('#datetimepicker').datetimepicker();
        $('#datetimepicker1').datetimepicker();
        $('#datetimepicker2').datetimepicker();
        $('#datetimepicker3').datetimepicker();
        $('#datetimepicker4').datetimepicker();
        $('#datetimepicker5').datetimepicker();
    });

    // disabling event buttons
    $('#addReservationBtn').hide();
    $('#updateEventBtn').hide();
    $('#deleteEventBtn').hide();


</script>
<script>
    let reservationsJSON = <% if (events != null) out.print(EventLookupBean.getEventRoomsJSON(events));
                              else out.print("{}");
                           %>;
</script>
</body>
</html>
