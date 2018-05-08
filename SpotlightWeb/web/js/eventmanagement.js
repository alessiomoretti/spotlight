var selectedEventID = null;
var selectedEventName = null;
var addedReservation = false;

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
        var tr = "<tr><td>"+reservation["roomName"]+"</td><td>"+reservation["roomDepartment"]+"</td><td>"+reservation["reservationStart"]+"</td><td>"+reservation["reservationEnd"]+"</td><td style='width:5%'><i class='fa fa-trash-alt' id='"+reservation["reservationID"]+"'/></td></tr>";
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
    $('#startTimeUpdateText').val(eS.toLocaleString("en-US"));
    $('#endTimeUpdateText').val(eE.toLocaleString("en-US"));
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