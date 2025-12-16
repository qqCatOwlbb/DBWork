document.addEventListener("DOMContentLoaded",function(){
  var token=localStorage.getItem("token");
  if(!token){window.location.href="/login.html";return;}
  var avatar=document.getElementById("avatar");
  var usernameEl=document.getElementById("username");
  var bioEl=document.getElementById("bio");
  var avatarFile=document.getElementById("avatar-file");
  var avatarUpload=document.getElementById("avatar-upload");
  var avatarErr=document.getElementById("avatar-error");
  var form=document.getElementById("profile-form");
  var err=document.getElementById("profile-error");
  var myArticles=document.getElementById("my-articles");
  var myLikes=document.getElementById("my-likes");
  var pageError=document.getElementById("error");
  document.getElementById("link-logout").addEventListener("click",async function(e){e.preventDefault();try{await fetch("/api/logout",{method:"DELETE",headers:{Authorization:token}});}catch(_){}localStorage.removeItem("token");window.location.href="/login.html"});

  async function getJSON(url,opts){var res=await fetch(url,opts||{});var data=await res.json();if(res.ok&&data&&data.code===200){return data.data}else{throw new Error((data&&data.msg)||(data&&data.message)||"请求失败")}}

  async function loadInfo(){try{var u=await getJSON("/api/user/getinfo",{headers:{Authorization:token}});usernameEl.textContent=u.username||"";bioEl.textContent=u.bio||"";if(u.avatar){avatar.src=u.avatar}else{avatar.src="/images/avatar-default.png"}}catch(e){pageError.textContent=e.message}}

  avatarUpload.addEventListener("click",async function(){avatarErr.textContent="";var f=avatarFile.files&&avatarFile.files[0];if(!f){avatarErr.textContent="请选择文件";return}var fd=new FormData();fd.append("file",f);try{var res=await fetch("/api/updateavatar",{method:"POST",headers:{Authorization:token},body:fd});var data=await res.json();if(!(res.ok&&data&&data.code===200)){throw new Error((data&&data.msg)||(data&&data.message)||"上传失败")}await loadInfo()}catch(e){avatarErr.textContent=e.message}});

  form.addEventListener("submit",async function(e){e.preventDefault();err.textContent="";var body={username:document.getElementById("inp-username").value.trim()||undefined,password:document.getElementById("inp-password").value.trim()||undefined,bio:document.getElementById("inp-bio").value.trim()||undefined};try{var res=await fetch("/api/update",{method:"POST",headers:{"Content-Type":"application/json",Authorization:token},body:JSON.stringify(body)});var data=await res.json();if(!(res.ok&&data&&data.code===200)){throw new Error((data&&data.msg)||(data&&data.message)||"更新失败")}await loadInfo();err.textContent="已保存"}catch(e){err.textContent=e.message}});

  function renderArticles(list,container){container.innerHTML="";if(!list||list.length===0){container.innerHTML="<div class='item'>暂无数据</div>";return}list.forEach(function(it){var wrap=document.createElement("div");wrap.className="item article-card";var aid=(it.id!=null?it.id:it.article_id);var title=document.createElement("h3");title.className="item-title";title.textContent=(it.title||"无标题");var meta=document.createElement("div");meta.className="item-meta";meta.textContent="浏览："+(it.view_count||0)+" | 点赞："+(it.like_count||0);var actions=document.createElement("div");actions.className="comment-actions";var link=document.createElement("a");link.href=(aid!=null?"/article-detail.html?id="+aid:"#");link.textContent="查看";link.className="btn secondary";var del=document.createElement("button");del.className="btn secondary";del.textContent="删除";del.addEventListener("click",function(){if(aid!=null){deleteArticle(aid)}});actions.appendChild(link);actions.appendChild(del);wrap.appendChild(title);wrap.appendChild(meta);wrap.appendChild(actions);wrap.addEventListener("click",function(){if(aid!=null){window.location.href="/article-detail.html?id="+aid}});container.appendChild(wrap)})}

  async function loadMyArticles(){try{var list=await getJSON("/api/user/getart",{headers:{Authorization:token}});renderArticles(list,myArticles)}catch(e){pageError.textContent=e.message}}
  async function loadMyLikes(){try{var list=await getJSON("/api/user/mylike",{headers:{Authorization:token}});renderArticles(list,myLikes)}catch(e){pageError.textContent=e.message}}

  async function deleteArticle(articleId){try{var res=await fetch("/api/article/delete?article_id="+articleId,{method:"DELETE",headers:{Authorization:token}});var data=await res.json();if(!(res.ok&&data&&data.code===200)){throw new Error((data&&data.msg)||(data&&data.message)||"删除失败")}await loadMyArticles()}catch(e){pageError.textContent=e.message}}

  (async function(){await loadInfo();await loadMyArticles();await loadMyLikes();})();
});
