document.addEventListener("DOMContentLoaded",function(){
  var token=localStorage.getItem("token");
  if(!token){window.location.href="/login.html";return;}
  var form=document.getElementById("publish-form");
  var err=document.getElementById("publish-error");
  document.getElementById("link-logout").addEventListener("click",async function(e){e.preventDefault();try{await fetch("/api/logout",{method:"DELETE",headers:{Authorization:token}});}catch(_){}localStorage.removeItem("token");window.location.href="/login.html"});
  form.addEventListener("submit",async function(e){
    e.preventDefault();
    err.textContent="";
    var title=document.getElementById("pub-title").value.trim();
    var content=document.getElementById("pub-content").value.trim();
    if(!title||!content){err.textContent="标题或内容不能为空";return;}
    // ArticlePublishDTO 期望顶层字段 title/content
    var body={title:title,content:content};
    try{
      var res=await fetch("/api/article/publish",{method:"POST",headers:{"Content-Type":"application/json",Authorization:token},body:JSON.stringify(body)});
      var data=await res.json();
      if(res.ok&&data&&data.code===200){
        window.location.href="/index.html";
      }else{
        err.textContent=(data&&data.msg)||(data&&data.message)||"发布失败";
      }
    }catch(e){err.textContent="网络错误";}
  });
});
