function match(){
	let password = document.getElementById("newPassword").value;
	let cnfpwd = document.getElementById("renewPassword").value;
	console.log(password,cnfpwd);
	
	let message = document.getElementById("message");
	if(password.length != 0)
	{
		if(password == cnfpwd)
		{
			console.log(password);
			message.textContent = "Password Match";
			message.style.backgroundColor = "#3ae374";
			
		}
		else{
			message.textContent = "Password doesnot Match";
			message.style.backgroundColor = "#ff4d4d";
		}
	}
}