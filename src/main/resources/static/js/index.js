document.addEventListener("DOMContentLoaded",function(){
  var token=localStorage.getItem("token");
  if(!token){window.location.href="/login.html";return;}
  var state={type:"like",page:1,pageSize:10};
  var likeBtn=document.getElementById("tab-like");
  var viewBtn=document.getElementById("tab-view");
  var prev=document.getElementById("prev");
  var next=document.getElementById("next");
  var list=document.getElementById("list");
  var errorEl=document.getElementById("error");
  var logout=document.getElementById("link-logout");
  function setActive(){
    likeBtn.classList.toggle("active",state.type==="like");
    viewBtn.classList.toggle("active",state.type==="view");
  }
  async function fetchArticles(){
    errorEl.textContent="";
    var url=state.type==="like"?"/api/article/select/bylike":"/api/article/select/byview";
    var qs="?page="+state.page+"&pageSize="+state.pageSize;
    try{
      var res=await fetch(url+qs,{headers:{Authorization:token}});
      var data=await res.json();
      if(res.ok&&data&&data.code===200){
        render(data.data||[]);
      }else{
        var msg=(data&&data.msg)||(data&&data.message)||"加载失败";
        errorEl.textContent=msg;
        if(res.status===401){localStorage.removeItem("token");window.location.href="/login.html";}
      }
    }catch(e){
      errorEl.textContent="网络错误";
    }
  }
  function render(items){
    list.innerHTML="";
    if(!items||items.length===0){list.innerHTML="<div class=\"item\">暂无数据</div>";return;}
    items.forEach(function(it){
      var el=document.createElement("article");
      el.className="item";
      var title=document.createElement("h2");
      title.className="item-title";
      title.textContent=(it.title||"无标题");
      var meta=document.createElement("div");
      meta.className="item-meta";
      meta.textContent=(it.username?("作者："+it.username):"");
      var content=document.createElement("div");
      content.className="item-content";
      var text=(it.content||"");
      var excerpt=text.length>160?text.slice(0,160)+"…":text;
      content.textContent=excerpt;
      el.appendChild(title);
      el.appendChild(meta);
      el.appendChild(content);
      el.addEventListener("click",function(){if(it.id!=null){window.location.href="/article-detail.html?id="+it.id;}});
      list.appendChild(el);
    });
  }
  likeBtn.addEventListener("click",function(){state.type="like";state.page=1;setActive();fetchArticles();});
  viewBtn.addEventListener("click",function(){state.type="view";state.page=1;setActive();fetchArticles();});
  prev.addEventListener("click",function(){if(state.page>1){state.page--;fetchArticles();}});
  next.addEventListener("click",function(){state.page++;fetchArticles();});
  logout.addEventListener("click",async function(e){e.preventDefault();try{var r=await fetch("/api/logout",{method:"DELETE",headers:{Authorization:token}});if(r.ok){localStorage.removeItem("token");window.location.href="/login.html";}else{localStorage.removeItem("token");window.location.href="/login.html";}}catch(err){localStorage.removeItem("token");window.location.href="/login.html";}});
  setActive();
  fetchArticles();
});
