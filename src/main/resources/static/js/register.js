document.addEventListener("DOMContentLoaded",function(){
  var form=document.getElementById("register-form");
  var errorEl=document.getElementById("error");
  form.addEventListener("submit",async function(e){
    e.preventDefault();
    errorEl.textContent="";
    var username=document.getElementById("username").value.trim();
    var password=document.getElementById("password").value.trim();
    try{
      var res=await fetch("/api/register",{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({username:username,password:password})});
      var data=await res.json();
      if(res.ok&&data&&data.code===200){
        window.location.href="/login.html";
      }else{
        var msg=data&&data.msg?data.msg:(data&&data.message?data.message:"注册失败");
        errorEl.textContent=msg;
      }
    }catch(err){
      errorEl.textContent="网络错误";
    }
  });
});
