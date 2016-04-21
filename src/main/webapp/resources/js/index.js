function resizeTables() {
  var runningGames = $('[id$=runnning_games]');
  runningGames.dataTable({
    "bFilter": false,
    "bInfo": false,
    "sScrollY": Math.min(400, runningGames.height()) + "px",
    "bPaginate": false,
    "aoColumnDefs": [
      {'bSortable': false, 'aTargets': [2]},
      {"sType": "natural", "aTargets": [0, 1]}
    ]
  });

  var completedGames = $('[id$=completed_games]');
  completedGames.dataTable({
    "bFilter": false,
    "bInfo": false,
    "sScrollY": Math.min(400, completedGames.height()) + "px",
    "bPaginate": false,
    "aoColumnDefs": [
      {'bSortable': false, 'aTargets': [2, 3]},
      {"sType": "natural", "aTargets": [0, 1]}
    ]
  });
}

$(document).ready(function () {
  resizeTables();
});
