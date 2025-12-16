document.addEventListener("DOMContentLoaded",function(){
  var form=document.getElementById("login-form");
  var errorEl=document.getElementById("error");
  form.addEventListener("submit",async function(e){
    e.preventDefault();
    errorEl.textContent="";
    var username=document.getElementById("username").value.trim();
    var password=document.getElementById("password").value.trim();
    try{
      var res=await fetch("/api/login",{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({username:username,password:password})});
      var data=await res.json();
      if(res.ok&&data&&data.code===200){
        var token=data.data;
        localStorage.setItem("token","Bearer "+token);
        window.location.href="/index.html";
      }else{
        var msg=data&&data.msg?data.msg:(data&&data.message?data.message:"登录失败");
        errorEl.textContent=msg;
      }
    }catch(err){
      errorEl.textContent="网络错误";
    }
  });
});
