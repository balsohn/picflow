// PicFlow JavaScript - Part 1

// 전역 변수
const API_BASE_URL = 'http://localhost:8080/api';
let currentUser = null;
let currentPage = 'home';
let authToken = localStorage.getItem('authToken');
let currentPostPage = 0;
let isLoadingPosts = false;
let hasMorePosts = true;

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    if (authToken) {
        loadUserProfile();
        showApp();
    } else {
        showAuthContainer();
    }

    setupEventListeners();
});

function setupEventListeners() {
    // 로그인 폼
    document.getElementById('loginForm').addEventListener('submit', handleLogin);

    // 회원가입 폼
    document.getElementById('signupForm').addEventListener('submit', handleSignup);

    // 프로필 수정 폼
    document.getElementById('profileForm').addEventListener('submit', handleProfileUpdate);

    // 네비게이션 클릭 이벤트
    document.querySelectorAll('[data-page]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            navigateToPage(page);
        });
    });

    // 게시물 작성 엔터키 이벤트
    document.getElementById('postContent').addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && e.ctrlKey) {
            createPost();
        }
    });

    // 검색 엔터키 이벤트
    document.getElementById('searchInput').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            searchUsers();
        }
    });

    // 무한 스크롤
    window.addEventListener('scroll', handleScroll);

    // 외부 클릭으로 메뉴 닫기
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.post-menu')) {
            document.querySelectorAll('.post-menu-dropdown').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal').forEach(modal => {
                if (modal.style.display === 'flex') {
                    modal.style.display = 'none';
                }
            });
        }
    });
}

// 무한 스크롤 처리
function handleScroll() {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 1000) {
        if (!isLoadingPosts && hasMorePosts && currentPage === 'home') {
            loadMorePosts();
        }
    }
}

// 더 많은 게시물 로드
async function loadMorePosts() {
    if (isLoadingPosts || !hasMorePosts) return;

    isLoadingPosts = true;
    currentPostPage++;

    try {
        const response = await fetch(`${API_BASE_URL}/posts?page=${currentPostPage}&size=10`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();

            if (data.data.posts.length === 0) {
                hasMorePosts = false;
            } else {
                appendPosts(data.data.posts, 'feed');
            }
        }
    } catch (error) {
        console.error('더 많은 게시물 로드 오류:', error);
    } finally {
        isLoadingPosts = false;
    }
}

// 인증 관련 함수들
async function handleLogin(e) {
    e.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        showLoading(true);
        clearFormErrors();

        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            authToken = data.data.token;
            localStorage.setItem('authToken', authToken);

            currentUser = {
                id: data.data.userId,
                username: data.data.username,
                email: email
            };

            hideModal('loginModal');
            showApp();
            showToast('로그인되었습니다!', 'success');
        } else {
            showFormError('loginEmailError', data.message);
        }
    } catch (error) {
        console.error('로그인 오류:', error);
        showToast('로그인 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

async function handleSignup(e) {
    e.preventDefault();

    const email = document.getElementById('signupEmail').value;
    const username = document.getElementById('signupUsername').value;
    const password = document.getElementById('signupPassword').value;

    try {
        showLoading(true);
        clearFormErrors();

        const response = await fetch(`${API_BASE_URL}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, username, password })
        });

        const data = await response.json();

        if (response.ok) {
            hideModal('signupModal');
            showModal('loginModal');
            showToast('회원가입이 완료되었습니다! 로그인해주세요.', 'success');

            // 회원가입한 이메일을 로그인 폼에 자동 입력
            document.getElementById('loginEmail').value = email;
        } else {
            showFormError('signupEmailError', data.message);
        }
    } catch (error) {
        console.error('회원가입 오류:', error);
        showToast('회원가입 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

async function handleProfileUpdate(e) {
    e.preventDefault();

    const username = document.getElementById('profileUsername').value;
    const bio = document.getElementById('profileBio').value;

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/users/profile`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ username, bio })
        });

        const data = await response.json();

        if (response.ok) {
            currentUser.username = data.data.username;
            currentUser.bio = data.data.bio;
            updateUserProfileUI();
            hideModal('profileModal');
            showToast('프로필이 수정되었습니다!', 'success');
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('프로필 수정 오류:', error);
        showToast('프로필 수정 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

function logout() {
    localStorage.removeItem('authToken');
    authToken = null;
    currentUser = null;
    showAuthContainer();
    showToast('로그아웃되었습니다.', 'success');
}

// UI 관련 함수들
function showApp() {
    document.getElementById('authContainer').style.display = 'none';
    document.getElementById('appContainer').classList.add('show');
    loadUserProfile();
    loadFeed();
}

function showAuthContainer() {
    document.getElementById('authContainer').style.display = 'flex';
    document.getElementById('appContainer').classList.remove('show');
}

function navigateToPage(page) {
    // 모든 페이지 숨기기
    document.querySelectorAll('.page-content').forEach(p => p.classList.remove('active'));

    // 모든 네비게이션 링크 비활성화
    document.querySelectorAll('.nav-link, .bottom-nav-link').forEach(link => link.classList.remove('active'));

    // 선택한 페이지 표시
    document.getElementById(page + 'Page').classList.add('active');

    // 해당 네비게이션 링크 활성화
    document.querySelectorAll(`[data-page="${page}"]`).forEach(link => link.classList.add('active'));

    currentPage = page;

    // 게시물 페이지 리셋
    currentPostPage = 0;
    hasMorePosts = true;

    // 페이지별 데이터 로드
    switch(page) {
        case 'home':
            loadFeed();
            break;
        case 'profile':
            loadMyProfile();
            break;
        case 'search':
            document.getElementById('searchResults').innerHTML = '';
            break;
    }
}

async function loadUserProfile() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/profile`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            currentUser = data.data;
            updateUserProfileUI();
        }
    } catch (error) {
        console.error('프로필 로드 오류:', error);
    }
}

function updateUserProfileUI() {
    if (!currentUser) return;

    const avatar = currentUser.username.charAt(0).toUpperCase();

    // 사이드바 업데이트
    document.getElementById('sidebarAvatar').textContent = avatar;
    document.getElementById('sidebarUsername').textContent = currentUser.username;
    document.getElementById('sidebarEmail').textContent = currentUser.email;

    // 프로필 페이지 업데이트
    document.getElementById('profileAvatarLarge').textContent = avatar;
    document.getElementById('profileName').textContent = currentUser.username;
    document.getElementById('profileBioText').textContent = currentUser.bio || '자기소개가 없습니다.';

    // 게시물 작성 아바타 업데이트
    document.getElementById('createPostAvatar').textContent = avatar;

    // 프로필 수정 폼 업데이트
    document.getElementById('profileUsername').value = currentUser.username;
    document.getElementById('profileBio').value = currentUser.bio || '';
}

// PicFlow JavaScript - Part 2 (이 부분을 Part 1 뒤에 이어붙여주세요)

// 게시물 관련 함수들
async function createPost() {
    const title = document.getElementById('postTitle').value.trim();
    const content = document.getElementById('postContent').value.trim();

    if (!title || !content) {
        showToast('제목과 내용을 모두 입력해주세요.', 'error');
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/posts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                title: title,
                content: content,
                imageUrl: null
            })
        });

        const data = await response.json();

        if (response.ok) {
            // 폼 초기화
            document.getElementById('postTitle').value = '';
            document.getElementById('postContent').value = '';
            document.getElementById('imageFile').value = '';
            removeImage();

            showToast('게시물이 작성되었습니다!', 'success');

            // 피드 새로고침
            currentPostPage = 0;
            hasMorePosts = true;
            loadFeed();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('게시물 작성 오류:', error);
        showToast('게시물 작성 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

async function loadFeed() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/posts?page=0&size=10`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderPosts(data.data.posts, 'feed');

            // 좋아요 상태 로드
            for (const post of data.data.posts) {
                await loadLikeStatus(post.postId);
            }
        }
    } catch (error) {
        console.error('피드 로드 오류:', error);
        showToast('피드를 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

// 게시물들을 기존 목록에 추가 (무한 스크롤용)
function appendPosts(posts, containerId) {
    const container = document.getElementById(containerId);

    posts.forEach(post => {
        const postElement = createPostElement(post);
        container.appendChild(postElement);

        // 좋아요 상태 로드
        loadLikeStatus(post.postId);
    });
}

async function loadMyProfile() {
    if (!currentUser) return;

    try {
        // 내 게시물 로드
        const response = await fetch(`${API_BASE_URL}/posts/user/${currentUser.id}?page=0&size=10`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderPosts(data.data.posts, 'myPostsFeed');

            // 게시물 수 업데이트
            document.getElementById('profilePostCount').textContent = data.data.totalElements;
        }

        // 팔로우 수 로드
        await loadFollowStats();
    } catch (error) {
        console.error('내 프로필 로드 오류:', error);
    }
}

async function loadFollowStats() {
    try {
        // 팔로워 수
        const followersResponse = await fetch(`${API_BASE_URL}/follows/followers/count`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        if (followersResponse.ok) {
            const followersData = await followersResponse.json();
            document.getElementById('profileFollowerCount').textContent = followersData.data;
        }

        // 팔로잉 수
        const followingResponse = await fetch(`${API_BASE_URL}/follows/following/count`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        if (followingResponse.ok) {
            const followingData = await followingResponse.json();
            document.getElementById('profileFollowingCount').textContent = followingData.data;
        }
    } catch (error) {
        console.error('팔로우 통계 로드 오류:', error);
    }
}

function createPostElement(post) {
    const article = document.createElement('article');
    article.className = 'post';
    article.setAttribute('data-post-id', post.postId);

    article.innerHTML = `
    <div class="post-header">
      <div class="post-author">
        <div class="profile-avatar">${post.author.charAt(0).toUpperCase()}</div>
        <div class="post-author-info">
          <h4>${post.author}</h4>
          <p>${formatDate(post.createdAt)}</p>
        </div>
      </div>
      ${post.authorId === currentUser?.id ? `
        <button class="post-menu" onclick="togglePostMenu(${post.postId})">
          <i class="fas fa-ellipsis-h"></i>
          <div class="post-menu-dropdown" id="menu-${post.postId}">
            <button class="post-menu-item" onclick="editPost(${post.postId})">수정</button>
            <button class="post-menu-item delete" onclick="deletePost(${post.postId})">삭제</button>
          </div>
        </button>
      ` : ''}
    </div>

    <div class="post-content">
      <div class="post-title">${post.title}</div>
      <p class="post-text">${post.content}</p>
      ${post.imageUrl ? `<img src="${post.imageUrl}" alt="게시물 이미지" class="post-image">` : ''}
    </div>

    <div class="post-footer">
      <div class="post-actions">
        <button class="post-action" onclick="toggleLike(${post.postId})" data-post-id="${post.postId}">
          <i class="far fa-heart"></i>
          <span>${post.likeCount}</span>
        </button>
        <button class="post-action" onclick="toggleComments(${post.postId})">
          <i class="far fa-comment"></i>
          <span>${post.commentCount}</span>
        </button>
      </div>
      <div class="post-stats">
        조회 ${Math.floor(Math.random() * 1000)}회
      </div>
    </div>

    <div class="comments-section" id="comments-${post.postId}">
      <div class="comment-form">
        <div class="profile-avatar">${currentUser?.username?.charAt(0).toUpperCase() || 'U'}</div>
        <div class="comment-input-container">
          <textarea class="comment-input" placeholder="댓글을 입력하세요..." id="comment-input-${post.postId}"></textarea>
          <button class="comment-submit" onclick="addComment(${post.postId})">등록</button>
        </div>
      </div>
      <div class="comments-list" id="comments-list-${post.postId}">
        <!-- 댓글들이 여기에 로드됩니다 -->
      </div>
    </div>
  `;

    return article;
}

function renderPosts(posts, containerId) {
    const container = document.getElementById(containerId);

    if (!posts || posts.length === 0) {
        container.innerHTML = `
      <div class="empty-state">
        <i class="fas fa-image"></i>
        <h3>게시물이 없습니다</h3>
        <p>첫 번째 게시물을 작성해보세요!</p>
      </div>
    `;
        return;
    }

    container.innerHTML = '';
    posts.forEach(post => {
        const postElement = createPostElement(post);
        container.appendChild(postElement);
    });
}

async function deletePost(postId) {
    if (!confirm('정말로 이 게시물을 삭제하시겠습니까?')) return;

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            showToast('게시물이 삭제되었습니다.', 'success');
            loadFeed();
            if (currentPage === 'profile') {
                loadMyProfile();
            }
        } else {
            const data = await response.json();
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('게시물 삭제 오류:', error);
        showToast('게시물 삭제 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

// 좋아요 관련 함수들
async function toggleLike(postId) {
    if (!currentUser) {
        showAuthModal();
        return;
    }

    try {
        // 현재 좋아요 상태 확인
        const statusResponse = await fetch(`${API_BASE_URL}/posts/${postId}/likes/status`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        const statusResult = await statusResponse.json();
        const isLiked = statusResult.data;

        const method = isLiked ? 'DELETE' : 'POST';

        const response = await fetch(`${API_BASE_URL}/posts/${postId}/likes`, {
            method: method,
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            // UI 업데이트
            const button = document.querySelector(`[data-post-id="${postId}"]`);
            const icon = button.querySelector('i');
            const countSpan = button.querySelector('span');

            if (isLiked) {
                button.classList.remove('liked');
                icon.className = 'far fa-heart';
                countSpan.textContent = parseInt(countSpan.textContent) - 1;
            } else {
                button.classList.add('liked');
                icon.className = 'fas fa-heart';
                countSpan.textContent = parseInt(countSpan.textContent) + 1;
            }
        }
    } catch (error) {
        console.error('좋아요 오류:', error);
    }
}

async function loadLikeStatus(postId) {
    if (!currentUser) return;

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/likes/status`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            const isLiked = data.data;

            const button = document.querySelector(`[data-post-id="${postId}"]`);
            if (button) {
                const icon = button.querySelector('i');

                if (isLiked) {
                    button.classList.add('liked');
                    icon.className = 'fas fa-heart';
                } else {
                    button.classList.remove('liked');
                    icon.className = 'far fa-heart';
                }
            }
        }
    } catch (error) {
        console.error('좋아요 상태 로드 오류:', error);
    }
}

// 댓글 관련 함수들
function toggleComments(postId) {
    const commentsSection = document.getElementById(`comments-${postId}`);
    if (commentsSection.classList.contains('show')) {
        commentsSection.classList.remove('show');
    } else {
        commentsSection.classList.add('show');
        loadComments(postId);
    }
}

async function loadComments(postId) {
    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments?page=0&size=10`);
        const result = await response.json();

        if (response.ok) {
            const commentsList = document.getElementById(`comments-list-${postId}`);
            const comments = result.data.comments;

            if (comments.length === 0) {
                commentsList.innerHTML = '<p style="text-align: center; color: #666; padding: 20px;">첫 번째 댓글을 작성해보세요!</p>';
            } else {
                commentsList.innerHTML = comments.map(comment => `
          <div class="comment">
            <div class="profile-avatar">${comment.author.charAt(0).toUpperCase()}</div>
            <div class="comment-content">
              <div class="comment-author">${comment.author}</div>
              <div class="comment-text">${comment.content}</div>
              <div class="comment-time">${formatDate(comment.createdAt)}</div>
            </div>
          </div>
        `).join('');
            }
        }
    } catch (error) {
        console.error('댓글 로드 오류:', error);
    }
}

async function addComment(postId) {
    if (!currentUser) {
        showAuthModal();
        return;
    }

    const commentInput = document.getElementById(`comment-input-${postId}`);
    const content = commentInput.value.trim();

    if (!content) {
        showToast('댓글 내용을 입력해주세요.', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ content })
        });

        if (response.ok) {
            commentInput.value = '';
            loadComments(postId);
            updateCommentCount(postId);
        } else {
            const result = await response.json();
            showToast(result.message || '댓글 작성에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('댓글 작성 오류:', error);
        showToast('댓글 작성 중 오류가 발생했습니다.', 'error');
    }
}

async function updateCommentCount(postId) {
    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments/count`);
        const result = await response.json();

        if (response.ok) {
            const button = document.querySelector(`[onclick="toggleComments(${postId})"]`);
            if (button) {
                const countSpan = button.querySelector('span');
                countSpan.textContent = result.data;
            }
        }
    } catch (error) {
        console.error('댓글 수 업데이트 오류:', error);
    }
}

// 검색 관련 함수들
async function searchUsers() {
    const query = document.getElementById('searchInput').value.trim();
    if (!query) return;

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/users/search/username?username=${encodeURIComponent(query)}&page=0&size=10`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            renderSearchResults(data.data.users);
        } else {
            showToast('검색 중 오류가 발생했습니다.', 'error');
        }
    } catch (error) {
        console.error('사용자 검색 오류:', error);
        showToast('검색 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
}

function renderSearchResults(users) {
    const container = document.getElementById('searchResults');

    if (!users || users.length === 0) {
        container.innerHTML = `
      <div class="empty-state">
        <i class="fas fa-search"></i>
        <h3>검색 결과가 없습니다</h3>
        <p>다른 키워드로 검색해보세요.</p>
      </div>
    `;
        return;
    }

    container.innerHTML = users.map(user => `
    <div class="user-result">
      <div class="user-info">
        <div class="profile-avatar">${user.username.charAt(0).toUpperCase()}</div>
        <div class="profile-details">
          <h4>${user.username}</h4>
          <p>${user.bio || '자기소개가 없습니다.'}</p>
        </div>
      </div>
      <button class="follow-button" onclick="toggleFollow(${user.userId}, this)">
        팔로우
      </button>
    </div>
  `).join('');
}

async function toggleFollow(userId, button) {
    if (!currentUser) {
        showAuthModal();
        return;
    }

    try {
        const isFollowing = button.classList.contains('following');
        const method = isFollowing ? 'DELETE' : 'POST';

        const response = await fetch(`${API_BASE_URL}/follows/${userId}`, {
            method: method,
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            if (isFollowing) {
                button.classList.remove('following');
                button.textContent = '팔로우';
                showToast('언팔로우했습니다.', 'success');
            } else {
                button.classList.add('following');
                button.textContent = '팔로잉';
                showToast('팔로우했습니다!', 'success');
            }
        } else {
            const data = await response.json();
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('팔로우 오류:', error);
        showToast('팔로우 중 오류가 발생했습니다.', 'error');
    }
}

// 이미지 관련 함수들
function previewImage(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];

        // 파일 크기 검증 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            showToast('파일 크기는 10MB 이하여야 합니다.', 'error');
            input.value = '';
            return;
        }

        // 파일 타입 검증
        if (!file.type.startsWith('image/')) {
            showToast('이미지 파일만 업로드 가능합니다.', 'error');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('previewImg').src = e.target.result;
            document.getElementById('imagePreview').style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
}

function removeImage() {
    document.getElementById('imageFile').value = '';
    document.getElementById('imagePreview').style.display = 'none';
    document.getElementById('previewImg').src = '';
}

function togglePostMenu(postId) {
    const menu = document.getElementById(`menu-${postId}`);
    const isVisible = menu.classList.contains('show');

    // 모든 메뉴 닫기
    document.querySelectorAll('.post-menu-dropdown').forEach(m => m.classList.remove('show'));

    // 클릭한 메뉴만 토글
    if (!isVisible) {
        menu.classList.add('show');
    }
}

function editPost(postId) {
    // 게시물 수정 기능 (선택사항)
    showToast('게시물 수정 기능은 추후 추가될 예정입니다.', 'info');
}

// 유틸리티 함수들
function showLoading(show) {
    document.getElementById('loading').style.display = show ? 'flex' : 'none';
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type}`;
    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function showModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

function hideModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    clearFormErrors();
}

function switchModal(fromModal, toModal) {
    hideModal(fromModal);
    showModal(toModal);
}

function showFormError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.textContent = message;
    errorElement.style.display = 'block';

    const input = errorElement.previousElementSibling;
    if (input) {
        input.classList.add('error');
    }
}

function clearFormErrors() {
    document.querySelectorAll('.form-error').forEach(error => {
        error.style.display = 'none';
        error.textContent = '';
    });

    document.querySelectorAll('.form-input').forEach(input => {
        input.classList.remove('error');
    });
}

function formatDate(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const diffInSeconds = Math.floor((now - date) / 1000);

    if (diffInSeconds < 60) {
        return '방금 전';
    } else if (diffInSeconds < 3600) {
        return `${Math.floor(diffInSeconds / 60)}분 전`;
    } else if (diffInSeconds < 86400) {
        return `${Math.floor(diffInSeconds / 3600)}시간 전`;
    } else if (diffInSeconds < 604800) {
        return `${Math.floor(diffInSeconds / 86400)}일 전`;
    } else {
        return date.toLocaleDateString('ko-KR');
    }
}