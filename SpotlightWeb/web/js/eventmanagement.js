var selectedEventID     = null;
var selectedEventName   = null;

var addedReservation    = false;
var createdEvent        = false;
var updatedEvent        = false;
var deletedEvent        = false;
var deletedReservation  = false;

function populateEventDetails(ID) {
    $('#reservationsTable tbody').html("");

    // populating selected event name
    let eventID   = ID.split("%")[0];
    let eventName = ID.split("%")[1];
    let eventMail = ID.split("%")[2];
    $('#selectedEvent').html(eventName);

    // selected event
    selectedEventID   = eventID;
    selectedEventName = eventName;

    // populating table
    reservationsJSON[eventID].forEach(function(reservation) {
        var tr = "<tr><td>"+reservation["roomName"]+"</td><td>"+reservation["roomDepartment"]+"</td><td>"+reservation["reservationStart"]+"</td><td>"+reservation["reservationEnd"]+"</td><td style='width:5%'><i class='fa fa-trash-alt' id='"+reservation["reservationID"]+"' onclick='deleteReservation(id); if (deletedReservation) location.reload()'/></td></tr>";
        $('#reservationsTable tbody').append(tr);
    });

    // enabling event buttons
    $('#addReservationBtn').show();
    $('#updateEventBtn').show();
    $('#deleteEventBtn').show();

    // retrieving event dates
    let eS = new Date(parseInt(ID.split("%")[3]));
    let eE = new Date(parseInt(ID.split("%")[4]));

    // populating event details
    $('#eventNameUpdate').val(eventName);
    $('#eventMailUpdate').val(eventMail);
    $('#startTimeUpdateText').val(eS.toLocaleString("en-US").replace(/:\d{2}\s/,' '));
    $('#endTimeUpdateText').val(eE.toLocaleString("en-US").replace(/:\d{2}\s/,' '));
}

function addReservation() {
    $('#alertErrorReservation').prop('hidden', true);
    $('#alertSuccessReservation').prop('hidden', true);


    // check on capacity
    if ($('#reservationCapacity').val().length === 0) {
        $('#alertErrorReservation').html("Capacity must be not emtpy");
        $('#alertErrorReservation').prop('hidden', false);
        return;
    }

    // check on dates
    if ($('#startTimeReservationText').val().length === 0 || $('#endTimeReservationText').val().length === 0) {
        $('#alertErrorReservation').html("Dates must be set");
        $('#alertErrorReservation').prop('hidden', false);
        return;
    }
    var startMillis = $('#datetimepicker4').data("datetimepicker").date().toDate().getTime();
    var endMillis = $('#datetimepicker5').data("datetimepicker").date().toDate().getTime();
    if (startMillis >= endMillis) {
        $('#alertErrorReservation').html("Start time must not be after end time");
        $('#alertErrorReservation').prop('hidden', false);
        return;
    }


    // preparing post JSON
    var postData = {
        "event_id"              : selectedEventID,
        "add_reservation"       : true,
        "capacity"              : $('#reservationCapacity').val(),
        "has_microphone"        : $('#microphoneCheck').prop('checked'),
        "has_projector"         : $('#projectorCheck').prop('checked'),
        "has_whiteboard"        : $('#whiteboardCheck').prop('checked'),
        "has_int_whiteboard"    : $('#intWhiteboardCheck').prop('checked'),
        "is_videocall_capable"  : $('#videocallCheck').prop('checked'),
        "department"            : $('#departmentSelect').val(),
        "start_timestamp"       : String(startMillis),
        "end_timestamp"         : String(endMillis),
    };
    if ($('#adminPrivileges').prop('checked'))
        postData["admin_privileges"] = true;

    // posting data
    $.ajax({
        url: "/AddReservationServlet",
        type: "POST",
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        data: $.param(postData),
        success: function (result) {
            if (result["status"] === "success") {
                $('#alertSuccessReservation').html(result["msg"]);
                $('#alertSuccessReservation').prop('hidden', false);
                addedReservation = true;
                $('#newReservationBtn').attr("disabled", "disabled");
                return;
            } else {
                $('#alertErrorReservation').html(result["error"]);
                $('#alertErrorReservation').prop('hidden', false);
                return;
            }
        },
        error: function () {
            $('#alertErrorReservation').html("Error submitting the reservation");
            $('#alertErrorReservation').prop('hidden', false);
            return;
        }
    });
}

function createNewEvent() {
    $('#alertErrorEvent').prop('hidden', true);
    $('#alertSuccessEvent').prop('hidden', true);

    // check on input parameters
    if ($('#eventNameCreate').val().length === 0 || $('#eventMailCreate').val().length === 0) {
        $('#alertErrorEvent').html("Name and mail must be not empty");
        $('#alertErrorEvent').prop('hidden', false);
        return;
    }

    // check on dates
    if ($('#startTimeCreateText').val().length === 0 || $('#endTimeCreateText').val().length === 0) {
        $('#alertErrorEvent').html("Dates must be set");
        $('#alertErrorEvent').prop('hidden', false);
        return;
    }
    var startMillis = $('#datetimepicker2').data("datetimepicker").date().toDate().getTime();
    var endMillis = $('#datetimepicker3').data("datetimepicker").date().toDate().getTime();
    if (startMillis >= endMillis) {
        $('#alertErrorEvent').html("Start time must not be after end time");
        $('#alertErrorEvent').prop('hidden', false);
        return;
    }

    // preparing post JSON
    var postData = {
        "create_event" : true,
        "event_name"   : $('#eventNameCreate').val(),
        "event_mail"   : $('#eventMailCreate').val(),
        "start_timestamp": String(startMillis),
        "end_timestamp"  : String(endMillis)
    };

    // posting data
    $.ajax({
        url: "/CreateEvent",
        type: "POST",
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        data: $.param(postData),
        success: function (result) {
            if (result["status"] === "success") {
                $('#alertSuccessEvent').html(result["msg"]);
                $('#alertSuccessEvent').prop('hidden', false);
                createdEvent = true;
                $('#createEventBtn').attr("disabled", "disabled");
                return;
            } else {
                $('#alertErrorEvent').html(result["error"]);
                $('#alertErrorEvent').prop('hidden', false);
                return;
            }
        },
        error: function () {
            $('#alertErrorEvent').html("Error submitting the reservation");
            $('#alertErrorEvent').prop('hidden', false);
            return;
        }
    });
}

function updateEvent() {
    // check on update parameters
    if ($('#eventNameUpdate').val().length === 0 || $('#eventMailUpdate').val().length === 0) {
        alert("Name and mail must be not empty");
        return;
    }

    // check on dates
    if ($('#startTimeUpdateText').val().length === 0 || $('#endTimeUpdateText').val().length === 0) {
        alert("Dates must be set");
        return;
    }
    var startMillis = $('#datetimepicker').data("datetimepicker").viewDate().toDate().getTime();
    var endMillis = $('#datetimepicker1').data("datetimepicker").viewDate().toDate().getTime();
    if (startMillis >= endMillis) {
        alert("Start time must not be after end time");
        return;
    }

    // preparing post JSON
    var postData = {
        "update_event" : true,
        "event_id"     : selectedEventID,
        "event_name"   : $('#eventNameUpdate').val(),
        "event_mail"   : $('#eventMailUpdate').val(),
        "start_timestamp": String(startMillis),
        "end_timestamp"  : String(endMillis)
    };

    // ask for user confirmation to modify the event
    if (confirm("Are you sure you want to modify this event?")) {
        $.ajax({
            url: "/UpdateEvent",
            type: "POST",
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            data: $.param(postData),
            success: function (result) {
                if (result["status"] === "success") {
                    alert(result["msg"]);
                    updatedEvent = true;
                    location.reload();
                    return;
                } else {
                    alert(result["error"]);
                    return;
                }
            },
            error: function () {
                alert("Error submitting the reservation");
                return;
            }
        });
    }
}

function deleteEvent() {
    // preparing post JSON
    var postData = {
        "delete_event" : true,
        "event_id"     : selectedEventID
    };

    // ask for user confirmation to delete the event
    if (confirm("Are you sure you want to delete this event?")) {
        $.ajax({
            url: "/DeleteItem",
            type: "POST",
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            data: $.param(postData),
            success: function (result) {
                if (result["status"] === "success") {
                    alert(result["msg"]);
                    deletedEvent = true;
                    location.reload();
                    return;
                } else {
                    alert(result["error"]);
                    return;
                }
            },
            error: function () {
                alert("Error submitting the reservation");
                return;
            }
        });
    }
}

function deleteReservation(resID) {
    // preparing post JSON
    var postData = {
        "delete_reservation" : true,
        "reservation_id"     : resID
    };

    // ask for user confirmation to delete the event
    if (confirm("Are you sure you want to delete this reservation?")) {
        $.ajax({
            url: "/DeleteItem",
            type: "POST",
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            data: $.param(postData),
            success: function (result) {
                if (result["status"] === "success") {
                    alert(result["msg"]);
                    deletedReservation = true;
                    location.reload();
                    return;
                } else {
                    alert(result["error"]);
                    return;
                }
            },
            error: function () {
                alert("Error submitting the reservation");
                return;
            }
        });
    }
}
