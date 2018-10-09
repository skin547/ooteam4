<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>



<h2>Shopping Cart</h2>

<div class="table-responsive">
  <table class="table table-striped table-bordered table-sm">

    <thead>
      <tr align="center">

        <th class="selectAndImage">
          <img 
            selectit="false" 
            class="selectAllItem" 
            src="img/site/cartNotSelected.png" 
            style="cursor: pointer">全選
        </th>
        <th>商品圖片</th>
        <th>商品名稱</th>
        <th>單價</th>
        <th>數量</th>
        <th>金額</th>
        <th>操作</th>

      </tr>
    </thead>

    <tbody>
      <c:forEach items="${ois }" var="oi">
        <tr oiid="${oi.id}" class="cartProductItemTR">

            <td align="left">
                <img 
                  selectit="false" 
                  oiid="${oi.id}" 
                  class="cartProductItemIfSelected" 
                  src="img/site/cartNotSelected.png" 
                  style="cursor: pointer">
                <a 
                  style="display: none" 
                  href="#nowhere">
                  <img 
                    src="img/site/cartSelected.png" 
                    style="cursor: pointer">
                </a>
            </td>

            <td align="center">
              <div style="position:relative; width: 80px; height: 80px;">
                <img 
                  class="cartProductImg"  
                  src="img/productSingle_middle/${oi.product.firstProductImage.id}.jpg" 
                  style="position:absolute; max-width: 100%; max-height: 100%; top:0; bottom: 0; left: 0; right: 0; margin: auto; ">
              </div>
            </td>

            <td align="center">
              <div class="cartProductLinkOutDiv">
                <a 
                  href="foreproduct?pid=${oi.product.id}" 
                  class="cartProductLink">${oi.product.name}</a>
              </div>
            </td>

            <td align="right">
              <span  
                class="cartProductItemPromotionPrice" 
                style="color:#c40000">
                $${oi.product.price}
              </span>
            </td>

            <td align="center">
              <div 
                class="cartProductChangeNumberDiv" 
                style="border: solid 1px #E5E5E5; width: 80px;">
                  <span 
                    class="hidden orderItemStock " 
                    pid="${ oi.product.id }">
                    ${ oi.product.inventory }
                  </span>
                  <span 
                    class="hidden orderItemPromotePrice" 
                    pid="${oi.product.id}">
                    ${ oi.product.price }
                  </span>
                  <a  
                    pid="${ oi.product.id }" 
                    oiid="${ oi.id }" 
                    class="numberMinus" 
                    href="#nowhere" 
                    style="text-decoration: none">-</a>
                  <input 
                    pid="${ oi.product.id }" 
                    oiid="${ oi.id }" 
                    class="orderItemNumberSetting" 
                    autocomplete="off" 
                    value="${ oi.quantity }" 
                    style="border: solid 1px #AAAAAA; width: 42px; display: inline-block;">
                  <a  
                    inventory="${ oi.product.inventory }" 
                    pid="${ oi.product.id }" 
                    oiid="${ oi.id }" 
                    class="numberPlus" 
                    href="#nowhere" 
                    style="text-decoration: none">+</a>
              </div>
            </td>

            <td align="right">
              <span 
                class="cartProductItemSmallSumPrice" 
                oiid="${ oi.id }" 
                pid="${ oi.product.id }">
                $<fmt:formatNumber type="number" 
                  value="${ oi.product.price * oi.quantity}" 
                  minFractionDigits="2"/>
              </span>
            </td>

            <td align="center">
              <a 
                class="deleteOrderItem" 
                oiid="${ oi.id }"  
                href="#nowhere">删除</a>
            </td>
        </tr>
      </c:forEach>
    </tbody>

  </table>
</div>


<div 
  class="cartFoot" 
  style="background-color: #E5E5E5; line-height: 50px; margin: 20px 0px; color: black; padding-left: 20px;">

    <div class="pull-right">
        <span>已選商品 <span class="cartSumNumber" >0</span> 件</span>
        <span>合計 (不含運費): </span>
        <span class="cartSumPrice" >$ 0.00</span>
        <button class="createOrderButton" disabled="disabled" >結 算</button>
    </div>

</div>



<script>
var deleteOrderItem = false;
var deleteOrderItemid = 0;

$(function(){

    $("a.deleteOrderItem").click(function(){
        deleteOrderItem = false;
        var oiid = $(this).attr("oiid")
        deleteOrderItemid = oiid;
        $("#deleteConfirmModal").modal('show');
    });

    $("button.deleteConfirmButton").click(function(){
        deleteOrderItem = true;
        $("#deleteConfirmModal").modal('hide');
    });

    $('#deleteConfirmModal').on('hidden.bs.modal', function (e) {
        if(deleteOrderItem){
            var page="foredeleteOrderItem";
            $.post(
                    page,
                    {"oiid":deleteOrderItemid},
                    function(result){
                        if("success" == result){
                            $("tr.cartProductItemTR[oiid="+deleteOrderItemid+"]").hide();
                        }
                        else{
                            location.href="login.jsp";
                        }
                    }
                );
        }
    })

    $("img.cartProductItemIfSelected").click(function(){
        var selectit = $(this).attr("selectit")
        if("selectit"==selectit){
            $(this).attr("src","img/site/cartNotSelected.png");
            $(this).attr("selectit","false")
            $(this).parents("tr.cartProductItemTR").css("background-color","#fff");
        }
        else{
            $(this).attr("src","img/site/cartSelected.png");
            $(this).attr("selectit","selectit")
            $(this).parents("tr.cartProductItemTR").css("background-color","#FFF8E1");
        }
        syncSelect();
        syncCreateOrderButton();
        calcCartSumPriceAndNumber();
    });

    $("img.selectAllItem").click(function(){
        var selectit = $(this).attr("selectit")
        if("selectit"==selectit){
            $("img.selectAllItem").attr("src","img/site/cartNotSelected.png");
            $("img.selectAllItem").attr("selectit","false")
            $(".cartProductItemIfSelected").each(function(){
                $(this).attr("src","img/site/cartNotSelected.png");
                $(this).attr("selectit","false");
                $(this).parents("tr.cartProductItemTR").css("background-color","#fff");
            });
        }
        else{
            $("img.selectAllItem").attr("src","img/site/cartSelected.png");
            $("img.selectAllItem").attr("selectit","selectit")
            $(".cartProductItemIfSelected").each(function(){
                $(this).attr("src","img/site/cartSelected.png");
                $(this).attr("selectit","selectit");
                $(this).parents("tr.cartProductItemTR").css("background-color","#FFF8E1");
            });
        }
        syncCreateOrderButton();
        calcCartSumPriceAndNumber();
    });

    $(".orderItemNumberSetting").keyup(function(){
        var pid = $(this).attr("pid");
        var oiid = $(this).attr("oiid");
        var inventory = $("span.orderItemStock[pid="+pid+"]").text();
        var price = $("span.orderItemPromotePrice[pid="+pid+"]").text();

        var num = $(".orderItemNumberSetting[pid=" + pid + "]").val();
        
        num = parseInt(num);
        if(isNaN(num))
            num = 1;
        if(num <= 0)
            num = 1;
        if(num > inventory)
            num = inventory;
    
        syncPrice(pid, num, price, oiid);
    });

    $(".numberPlus").click(function(){
        var pid=$(this).attr("pid");
        var oiid=$(this).attr("oiid");
        var inventory= $("span.orderItemStock[pid="+pid+"]").text();
        var price= $("span.orderItemPromotePrice[pid="+pid+"]").text();
        var num= $(".orderItemNumberSetting[pid="+pid+"]").val();
        
        num++;
        if(num>inventory)
            num = inventory;
        syncPrice(pid, num, price, oiid);
    });
    $(".numberMinus").click(function(){
        var pid=$(this).attr("pid");
        var inventory= $("span.orderItemStock[pid="+pid+"]").text();
        var price= $("span.orderItemPromotePrice[pid="+pid+"]").text();
        var oiid=$(this).attr("oiid");
        var num= $(".orderItemNumberSetting[pid="+pid+"]").val();
        
        --num;
        if(num<=0)
            num=1;
        syncPrice(pid, num, price, oiid);
    });

    $("button.createOrderButton").click(function(){
        var params = "";
        $(".cartProductItemIfSelected").each(function(){
            if("selectit" == $(this).attr("selectit")){
                var oiid = $(this).attr("oiid");
                params += "&oiid="+oiid;
            }
        });
        params = params.substring(1);
        location.href = "forebuy?" + params;
    });

})

function syncCreateOrderButton(){
    var selectAny = false;
    $(".cartProductItemIfSelected").each(function(){
        if("selectit"==$(this).attr("selectit")){
            selectAny = true;
        }
    });

    if(selectAny){
        $("button.createOrderButton").css("background-color","#C40000");
        $("button.createOrderButton").removeAttr("disabled");
    }
    else{
        $("button.createOrderButton").css("background-color","#AAAAAA");
        $("button.createOrderButton").attr("disabled","disabled");
    }

}
function syncSelect(){
    var selectAll = true;
    $(".cartProductItemIfSelected").each(function(){
        if("false"==$(this).attr("selectit")){
            selectAll = false;
        }
    });

    if(selectAll)
        $("img.selectAllItem").attr("src","img/site/cartSelected.png");
    else
        $("img.selectAllItem").attr("src","img/site/cartNotSelected.png");

}

function calcCartSumPriceAndNumber(){
    var sum = 0;
    var totalNumber = 0;
	
    $("img.cartProductItemIfSelected[selectit='selectit']").each(
      function (){
        var oiid = $(this).attr("oiid");
        var price = $(".cartProductItemSmallSumPrice[oiid=" + oiid + "]").text();
        price = price.replace(/,/g, "");    
        price = price.replace(/\\$/g, "");
        sum += new Number(price);
        
        var num = $(".orderItemNumberSetting[oiid=" + oiid + "]").val();
        totalNumber += new Number(num);
      }
    );

    $("span.cartSumPrice").html("$" + formatMoney(sum));
    $("span.cartSumNumber").html(totalNumber);
}

function syncPrice(pid, num, price, oiid){
    $(".orderItemNumberSetting[pid=" + pid + "]").val(num);
    var cartProductItemSmallSumPrice = formatMoney(num * price);
    $(".cartProductItemSmallSumPrice[pid=" + pid + "]").html("$" + cartProductItemSmallSumPrice);
    calcCartSumPriceAndNumber();

    var page = "forechangeOrderItem";
    $.post(
        page,
        {"pid": pid, "num": num, "oiid": oiid},
        function(result){
          if("success" != result){
            location.href="login.jsp";
          }
        }
      );
}
</script>
