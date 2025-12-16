document.addEventListener("DOMContentLoaded", function () {
    var token = localStorage.getItem("token");
    // 如果没有token，跳转登录
    if (!token) {
        console.warn("No token found, redirecting to login.");
        window.location.href = "/login.html";
        return;
    }

    var qs = new URLSearchParams(location.search);
    var articleId = Number(qs.get("id"));

    // 页面元素获取
    var titleEl = document.getElementById("title");
    var metaEl = document.getElementById("meta");
    var contentEl = document.getElementById("content");
    var likeBtn = document.getElementById("like-btn");
    var likeCountEl = document.getElementById("like-count");
    var errorEl = document.getElementById("error");
    var commentList = document.getElementById("comment-list");
    var editorInput = document.getElementById("comment-input");
    var editorSubmit = document.getElementById("comment-submit");
    var editorError = document.getElementById("comment-error");

    var currentUserId = null;

    if (!articleId) {
        if(errorEl) errorEl.textContent = "缺少文章ID参数";
        return;
    }

    // 通用请求函数
    async function getJSON(url, options) {
        var res = await fetch(url, options || {});
        var data = await res.json();
        // 兼容 code=200 或 success=true
        if (res.ok && data && (data.code === 200 || data.success)) {
            return data.data;
        } else {
            var msg = (data && data.msg) || (data && data.message) || "请求失败";
            throw new Error(msg);
        }
    }

    // 加载当前用户信息
    async function loadUser() {
        try {
            var data = await getJSON("/api/user/getinfo", { headers: { Authorization: token } });
            currentUserId = data && data.id;
        } catch (e) { console.error("Load user failed", e); }
    }

    // 加载文章详情
    async function loadArticle() {
        try {
            // 注意：你的后端 ArticleController 要求 POST 且 id 在 query string
            var res = await fetch("/api/article/view?article_id=" + articleId, {
                method: "POST",
                headers: { Authorization: token }
            });
            var json = await res.json();
            var data = (json && json.data) != null ? json.data : json;

            var title = data.title || data.article_title || "无标题";
            var content = data.content || data.article_content || "";
            var username = data.username || data.author || "";
            var viewCount = data.view_count || data.views || 0;
            var created = data.created_at || data.create_at || data.published_at || "";

            if(titleEl) titleEl.textContent = title;
            if(metaEl) {
                var authorText = username ? ("作者：" + username) : "";
                var createdText = created ? (" | 时间：" + created) : "";
                var viewText = " | 浏览：" + viewCount;
                metaEl.textContent = authorText + createdText + viewText;
            }
            if(contentEl) contentEl.innerHTML = content;

            if (!(json && json.code === 200)) {
                throw new Error((json && json.msg) || (json && json.message) || "加载文章失败");
            }
        } catch (e) {
            if(errorEl) errorEl.textContent = "加载文章异常: " + e.message;
        }
    }

    // 加载点赞数
    async function loadLikeCount() {
        try {
            var n = await getJSON("/api/article/getlike?article_id=" + articleId, { headers: { Authorization: token } });
            if(likeCountEl) likeCountEl.textContent = n;
        } catch (e) { }
    }

    // 检查是否点赞
    async function checkLiked() {
        try {
            var list = await getJSON("/api/user/mylike", { headers: { Authorization: token } });
            var liked = list && list.some(function (a) { return a.id === articleId });
            if(likeBtn) likeBtn.classList.toggle("active", liked);
        } catch (e) { }
    }

    // 点赞切换
    async function toggleLike() {
        if(!likeBtn) return;
        var liked = likeBtn.classList.contains("active");
        try {
            if (liked) {
                await getJSON("/api/article/like?article_id=" + articleId, { method: "DELETE", headers: { Authorization: token } });
            } else {
                await getJSON("/api/article/like?article_id=" + articleId, { method: "POST", headers: { Authorization: token } });
            }
            await loadLikeCount();
            await checkLiked();
        } catch (e) {
            if(errorEl) errorEl.textContent = e.message;
        }
    }

    // 加载一级评论
    async function loadParents() {
        try {
            var list = await getJSON("/api/comment/parent?article_id=" + articleId, { headers: { Authorization: token } });
            renderParents(list || []);
        } catch (e) {
            if(errorEl) errorEl.textContent = "加载评论失败: " + e.message;
        }
    }

    // 渲染一级评论
    function renderParents(items) {
        if(!commentList) return;
        commentList.innerHTML = "";
        if (!items || items.length === 0) {
            commentList.innerHTML = "<div class='comment'>暂无评论</div>";
            return;
        }
        items.forEach(function (c) {
            // 修复 Bug 1：如果 c.id 是 null，说明后端返回的数据有问题，跳过渲染防止报错
            if (!c.id) {
                console.warn("Found comment with null ID, skipping:", c);
                return;
            }

            var box = document.createElement("div");
            box.className = "comment";

            var header = document.createElement("div");
            header.className = "comment-header";
            header.textContent = (c.username || "匿名") + " • " + (c.create_at || "");

            var content = document.createElement("div");
            content.className = "comment-content";
            content.textContent = c.content || "";

            var actions = document.createElement("div");
            actions.className = "comment-actions";

            var viewBtn = document.createElement("button");
            viewBtn.className = "btn secondary";
            viewBtn.textContent = "查看回复";
            // 绑定事件时，因为上面检查了 c.id，这里不会传 null
            viewBtn.addEventListener("click", function () { loadChildren(c.id, children); });

            var replyBtn = document.createElement("button");
            replyBtn.className = "btn secondary";
            replyBtn.textContent = "回复";
            replyBtn.addEventListener("click", function () { spawnReplyEditor(c.id, children); });

            actions.appendChild(viewBtn);
            actions.appendChild(replyBtn);

            // 删除按钮
            if (currentUserId && c.user_id === currentUserId) {
                var delBtn = document.createElement("button");
                delBtn.className = "btn secondary";
                delBtn.textContent = "删除";
                delBtn.addEventListener("click", function () { deleteComment(c.id); });
                actions.appendChild(delBtn);
            }

            var children = document.createElement("div");
            children.className = "comment-children"; // 子评论容器

            box.appendChild(header);
            box.appendChild(content);
            box.appendChild(actions);
            box.appendChild(children);
            commentList.appendChild(box);
        });
    }

    // 加载子评论 (Bug 1 修复点：确保 parentId 传递正确)
    async function loadChildren(parentId, container) {
        if (!parentId) {
            container.innerHTML = "无法加载回复：ID丢失";
            return;
        }
        container.innerHTML = "加载中...";
        try {
            var list = await getJSON("/api/comment/son?parent_comment_id=" + parentId, { headers: { Authorization: token } });
            container.innerHTML = "";
            if (!list || list.length === 0) {
                container.innerHTML = "<div style='color:#999;font-size:0.9em;padding:5px;'>暂无回复</div>";
                return;
            }
            list.forEach(function (c) {
                var child = document.createElement("div");
                child.className = "comment child-comment"; // 可以在CSS里给 child-comment 加个 margin-left
                child.style.marginLeft = "20px";
                child.style.borderLeft = "2px solid #eee";
                child.style.paddingLeft = "10px";

                var header = document.createElement("div");
                header.className = "comment-header";
                header.textContent = (c.username || "匿名") + " • " + (c.create_at || "");

                var content = document.createElement("div");
                content.className = "comment-content";
                content.textContent = c.content || "";

                var actions = document.createElement("div");
                actions.className = "comment-actions";

                // 子评论也可以回复（实际上还是回复父评论，或者这里你可以改成 parentId 还是传当前子评论ID视业务逻辑而定）
                // 通常子评论的回复还是挂在父评论名下，或者这里简单处理：回复子评论也传 parentId
                var replyBtn = document.createElement("button");
                replyBtn.className = "btn secondary";
                replyBtn.textContent = "回复";
                replyBtn.addEventListener("click", function () { spawnReplyEditor(parentId, container); });
                actions.appendChild(replyBtn);

                if (currentUserId && c.user_id === currentUserId) {
                    var delBtn = document.createElement("button");
                    delBtn.className = "btn secondary";
                    delBtn.textContent = "删除";
                    delBtn.addEventListener("click", function () { deleteComment(c.id); });
                    actions.appendChild(delBtn);
                }

                child.appendChild(header);
                child.appendChild(content);
                child.appendChild(actions);
                container.appendChild(child);
            });
        } catch (e) {
            container.innerHTML = "加载回复失败: " + e.message;
        }
    }

    // 显示回复输入框
    function spawnReplyEditor(parentId, container) {
        if (container.querySelector(".comment-editor")) return; // 防止重复

        var wrap = document.createElement("div");
        wrap.className = "comment-editor";
        wrap.style.marginTop = "10px";

        var input = document.createElement("textarea");
        input.placeholder = "请输入回复内容...";
        input.style.width = "100%";
        input.style.height = "60px";

        var submit = document.createElement("button");
        submit.className = "btn primary";
        submit.textContent = "提交回复";
        submit.style.marginTop = "5px";

        var err = document.createElement("div");
        err.className = "error";

        wrap.appendChild(input);
        wrap.appendChild(submit);
        wrap.appendChild(err);

        // 插入到容器最前面
        container.prepend(wrap);

        submit.addEventListener("click", async function () {
            var text = input.value.trim();
            if (!text) {
                err.textContent = "内容不能为空";
                return;
            }
            try {
                await publishComment(text, parentId);
                wrap.remove();
                await loadChildren(parentId, container); // 刷新子评论
            } catch (e) {
                err.textContent = e.message;
            }
        });
    }

    // 发表评论 (API)
    async function publishComment(text, parentId) {
        var body = {
            article_id: articleId,
            content: text,
            parent_comment_id: parentId || null // 如果是主评，传null
        };
        var res = await fetch("/api/comment/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: token
            },
            body: JSON.stringify(body)
        });
        var data = await res.json();
        if (!(res.ok && data && (data.code === 200 || data.success))) {
            var msg = (data && data.msg) || (data && data.message) || "发表失败";
            throw new Error(msg);
        }
    }

    // 删除评论
    async function deleteComment(id) {
        if (!confirm("确定删除?")) return;
        try {
            await getJSON("/api/comment/delete?comment_id=" + id, { method: "DELETE", headers: { Authorization: token } });
            // 删除后刷新整个列表（简单粗暴，或者可以只移除DOM）
            await loadParents();
        } catch (e) {
            alert(e.message);
        }
    }

    // 主评论框提交事件
    if(editorSubmit) {
        editorSubmit.addEventListener("click", async function () {
            var text = editorInput.value.trim();
            if (!text) {
                if(editorError) editorError.textContent = "内容不能为空";
                return;
            }
            try {
                await publishComment(text, null); // null 表示是一级评论
                editorInput.value = "";
                if(editorError) editorError.textContent = "";
                await loadParents();
            } catch (e) {
                if(editorError) editorError.textContent = e.message;
            }
        });
    }

    if(likeBtn) likeBtn.addEventListener("click", toggleLike);

    var logoutBtn = document.getElementById("link-logout");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", async function (e) {
            e.preventDefault();
            try {
                await fetch("/api/logout", { method: "DELETE", headers: { Authorization: token } });
            } catch (err) { }
            localStorage.removeItem("token");
            window.location.href = "/login.html";
        });
    }

    // 初始化
    (async function () {
        await loadUser();
        await loadArticle();
        await loadLikeCount();
        await checkLiked();
        await loadParents();
    })();
});