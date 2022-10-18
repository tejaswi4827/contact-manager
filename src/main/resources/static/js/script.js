

const toggleSidebar = () => {
	if($(".sidebar").is(":visible"))
	{
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else{
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

// serch continer javascript
const search = () =>{

console.log("searching...")

let query =$("#search-input").val();
if(query=='')
{
$(".search-result").hide();
}
else{
	//search
	console.log(query);
	//sending request to server
	let url=`http://localhost:8080/search/${query}`;
	fetch(url).then((response)=>{
		return response.json();
	})
	.then((data)=>
	{
//data
let text=`<div class ='list-group'>`
data.forEach((contact) => {
	text += `<a href ='/user/${contact.cid}/contact' class= 'list-group-item list-group-item-action'>${contact.name} </a>`
});
text += `</div>`;
$(".search-result").html(text);
$(".search-result").show();
	});
	
}
};




//first request to server to create order id

const paymentStart = () => {

	console.log("start");
	let amount =$("#payment_field").val();
	if(amount =='' || amount == null)
	{
		//alert("amount is required!!");
		swal("Failed!", "amount is required!!!", "error");
		return;

	}


//code to send request to server using ajax to create order -jquery

$.ajax({
	url:"/user/create_order",
	data:JSON.stringify({amount:amount,info:'order_request'}),
	contentType:'application/json',
	type:'POST',
	dataType:'json',
	success:function(response){
		//this  function is invoked when success
		console.log(response);
		if(response.status == "created")
		{
			//open payment form
			let options ={
				"key": "rzp_live_eNJbsJPiKkhdpM", // Enter the Key ID generated from the Dashboard
"amount": response.amount, // Amount is in currency subunits. Default currency is

"currency": "INR",
"name": "Ticketindiscount",
"description": "support us",
"image": "https://assets.travclan.com/unsafe/0x100/smart/https://s3.ap-south-1.amazonaws.com/com.travclan.b2b2c/logo/4552/1659319952.360917/0.0372951131964373/received_615448026571158.webp",
"order_id": response.id,

handler:function(response){
	console.log(response.razorpay_payment_id);
	console.log(response.razorpay_order_id);
	console.log(response.razorpay_signature);
	swal("Good job!", "payment successful!!!", "success");
},
prefill: {
	"name": "",
	"email": "",
	"contact": ""
	},
	"notes": {
		"address": "Ticketindiscount"
		
		},
		"theme": {
		"color": "#3399cc"
		},

			};
			let rzp =  new Razorpay(options);
			
			rzp.on('payment.failed', function (response){
				console.log(response.error.code);
				console.log(response.error.description);
				console.log(response.error.source);
				console.log(response.error.step);
				console.log(response.error.reason);
				console.log(response.error.metadata.order_id);
				console.log(response.error.metadata.payment_id);
				swal("Failed!", "Oops payment failed!!!", "error");

			
				});
				rzp.open();
		}
	},
	error:function(error)
	{
		//invoked when error
		alert("something went wrong");
	},

});
	
};