$(document).ready( function(){ $('#datatable').DataTable({ stateSave: true , stateDuration: 3600, pageLength: 100 , lengthMenu: [[ 10, 25, 50, 100, -1 ],[ 10, 25, 50, 100, 'All'] ] , paging: true ,  order: [[ 0, 'desc' ]] , dom: 'C<\"clear\">Rlfrtip'   });});