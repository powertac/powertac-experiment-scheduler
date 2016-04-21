function updateScheduler(data) {
  if (data['text'] != undefined) {
    $('#adminControls\\:scheduler').html(data['text']);
  } else {
    $('#adminControls\\:scheduler').html("");
  }
}

function resizeTables() {
  $('[id$=dataPoms]').dataTable({
    "bFilter": false,
    "bInfo": false,

    "bPaginate": false
  });

  $('[id$=dataMachines]').dataTable({
    "bFilter": false,
    "bInfo": false,

    "bPaginate": false,
    "aoColumnDefs": [
      {'bSortable': false, 'aTargets': [2, 3, 4, 5, 6]},
      {"sType": "natural", "aTargets": [0, 1]}
    ]
  });

  $('[id$=dataUsers]').dataTable({
    "bFilter": false,
    "bInfo": false,

    "bPaginate": false,
    "aoColumnDefs": [
      {'bSortable': false, 'aTargets': [3]},
    ]
  });
}

function updateTables() {
  $.ajax({
    url: "Rest?type=scheduler",
    success: updateScheduler
  });
}

$(document).ready(function () {
  resizeTables();
  updateTables();
  setInterval(updateTables, 3000);
});