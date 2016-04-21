function resizeTables() {
  var paramsGlobal = $('[id$=paramsGlobal]');
  paramsGlobal.DataTable({
    bFilter: false,
    bInfo: false,
    paging: false,
    aoColumnDefs: [{'bSortable': false, 'aTargets': [0, 1, 2]}],
    aaSorting: []
  });

  var paramsServer = $('[id$=paramsServer]');
  paramsServer.DataTable({
    bFilter: false,
    bInfo: false,
    paging: false,
    scrollY: 200,
    aoColumnDefs: [{'bSortable': false, 'aTargets': [0, 1]}],
    aaSorting: []
  });
}

$(document).ready(function () {
  resizeTables();
});
