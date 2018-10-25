<div  class="col-sm-12" style="background-color:#f1f1f1;min-height:470px;height:auto">
<div class="justify-content-center" style="top:0px;width:75%;min-height:420px;height:100%;margin:auto;padding-left:20px;background-color:white">
<h1>Subscription</h1>
<table id="product_table" style="width:100%">
  <tr>
    <th class="col-sm-5">name</th>
    <th class="col-sm-4">state</th>
    <th class="col-sm-2"></th>
  </tr>
  <%! int i=1; %>
  <c:forEach items="${products}" var="p">
  
  <tr style="width:100%" id="row_<%= i %>">
	  <td class="col-sm-5" style="text-align:left" >
	 	 <a href="foreproduct?pid=${p.id}">${p.name}</a> 
	  </td>
	  
	  <c:if test="${p.inventory!=0}" >
		  <td class="col-sm-2" style="text-align:left">In Stock (Inventory Quantity: ${ p.inventory })</td>
	  </c:if>
	  
	   <c:if test="${p.inventory==0}" >
		  <td class="col-sm-2" style="text-align:left">Out Of Stock (Inventory Quantity: ${ p.inventory })</td>
	  </c:if>
	  
 	 <td class="col-sm-2">
	 	 <button onclick="deleteSubscription(<%= i %>,${p.id})" type="button" class="btn btn btn-warning ">
	 	 	<i class="fa fa-trash" aria-hidden="true"></i>
	 	 </button>
 	</td>
  </tr>
  
  <% i++; %>
  </c:forEach>
  </table>

  </div>
</div>

<script>
function deleteSubscription(i, pid){
	  console.log(i);
	  var page = "foredeleteSubscription";
	  $.get(
             page,
             {"pid":pid},
             function(result){
                 if("success" == result){
                	 $("#row_"+i).remove();               
                 }else{
                	
                 }
             
             });
	
}
</script>