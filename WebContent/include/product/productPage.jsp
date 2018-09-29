<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
    
 	
<div class="col-sm-4" style="text-align:left;">
	<div style="position:relative; width:225px; height: 225px;">
     	<img style="position:absolute; max-width: 100%; max-height: 100%; top:0; bottom: 0; left: 0; right: 0; margin: auto; " src="img/productSingle/${p.firstProductImage.id}.jpg">
    </div>

	<h1>${p.name}</h1>
	<h3>原價: $<fmt:formatNumber type="number" value="${p.originalPrice}" minFractionDigits="2"/></h3>
	<h3>促銷價: $<fmt:formatNumber type="number" value="${p.promotePrice}" minFractionDigits="2"/></h3>
	
        
	<p>庫存 ${p.stock} 件</p>
	
	Quantities:
	<div class="form-group" style="width:30%">
		<input class="form-control productNumberSetting" value="1" type="number" name="quantity" min="1" max="${p.stock}"></br>
	</div>
	
	<Span><i class="glyphicon glyphicon-info-sign"></i>Product Information</Span>
	
	<a href="#nowhere" class="addCartLink">
		<button class="btn btn-warning col-sm-9 addCartButton" type="button" id="addToCart">
			<i class="fa fa-shopping-cart"></i> Add To Cart
		</button>
	</a>
	
	<button class="btn btn-danger col-sm-9" type="button" id="subscribe">
		<i class="fa fa-heart"></i> Subscribe
	</button>
	
</div> 

 
<script>
$(function(){
    var stock = ${p.stock};
    $(".productNumberSetting").keyup(function(){
        var num= $(".productNumberSetting").val();
        num = parseInt(num);
        if(isNaN(num))
            num= 1;
        if(num<=0)
            num = 1;
        if(num>stock)
            num = stock;
        $(".productNumberSetting").val(num);
    });
     
     
    $(".addCartLink").click(function(){
        var page = "forecheckLogin";
        $.get(
                page,
                function(result){
                    if("success" == result){
                        var pid = ${p.id};
                        var num= $(".productNumberSetting").val();
                        var addCartpage = "foreaddCart";
                        $.get(
                                addCartpage,
                                {"pid":pid,"num":num},
                                function(result){
                                    if("success" == result){
                                        $(".addCartButton").html("已加入購物車");
                                        $(".addCartButton").attr("disabled","disabled");
                                        $(".addCartButton").css("background-color","lightgray")
                                        $(".addCartButton").css("border-color","lightgray")
                                        $(".addCartButton").css("color","black")
                                         
                                    }
                                    else{
                                         
                                    }
                                }
                        );                          
                    }
                    else{
                        $("#loginModal").modal('show');                     
                    }
                }
        );      
        return false;
    });
    $(".buyLink").click(function(){
        var page = "forecheckLogin";
        $.get(
                page,
                function(result){
                    if("success"==result){
                        var num = $(".productNumberSetting").val();
                        location.href= $(".buyLink").attr("href")+"&num="+num;
                    }
                    else{
                        $("#loginModal").modal('show');                     
                    }
                }
        );      
        return false;
    });
     
    $("button.loginSubmitButton").click(function(){
        var name = $("#name").val();
        var password = $("#password").val();
         
        if(0==name.length||0==password.length){
            $("span.errorMessage").html("請輸入帳號密碼");
            $("div.loginErrorMessageDiv").show();           
            return false;
        }
         
        var page = "foreloginAjax";
        $.get(
                page,
                {"name":name,"password":password},
                function(result){
                    if("success"==result){
                        location.reload();
                    }
                    else{
                        $("span.errorMessage").html("帳號密碼錯誤");
                        $("div.loginErrorMessageDiv").show();                       
                    }
                }
        );          
         
        return true;
    });
     
});
 
</script>
