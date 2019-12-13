# spring security的简单原理：

使用众多的拦截器对url拦截，以此来管理权限。但是这么多拦截器，笔者不可能对其一一来讲，主要讲里面核心流程的两个。

首先，权限管理离不开登陆验证的，所以登陆验证拦截器AuthenticationProcessingFilter要讲； 还有就是对访问的资源管理吧，所以资源管理拦截器AbstractSecurityInterceptor要讲；但拦截器里面的实现需要一些组件来实现，所以就有了AuthenticationManager、accessDecisionManager等组件来支撑。
## 现在先大概过一遍整个流程：
>用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现，而且AuthenticationManager会调用ProviderManager来获取用户验证信息（不同的Provider调用的服务不同，因为这些信息可以是在数据库上，可以是在LDAP服务器上，可以是xml配置文件上等），如果验证通过后会将用户的权限信息封装一个User放到spring的全局缓存SecurityContextHolder中，以备后面访问资源时使用。

>访问资源（即授权管理），访问url时，会通过AbstractSecurityInterceptor拦截器拦截，其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，在调用授权管理器AccessDecisionManager，这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错并调用权限不足页面。


 * [security过滤器链分析](https://blog.csdn.net/dushiwodecuo/article/details/78913113)

# 方案一
1. 创建用户表,角色表,资源表,用户角色表,角色资源表
## security 需实现以下类
### 2.1 自定义 Spring Security 配置
<details>
<summary>查看代码</summary>

```java
/**
prePostEnabled :决定Spring Security的前注解是否可用 [@PreAuthorize,@PostAuthorize,..]
secureEnabled : 决定是否Spring Security的保障注解 [@Secured] 是否可用
jsr250Enabled ：决定 JSR-250 annotations 注解[@RolesAllowed..] 是否可用.
 */
@Configurable
@EnableWebSecurity
//开启 Spring Security 方法级安全注解 @EnableGlobalMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 静态资源设置
     */
    @Override
    public void configure(WebSecurity webSecurity) {
        //不拦截静态资源,所有用户均可访问的资源
        webSecurity.ignoring().antMatchers(
                "/",
                "/css/**",
                "/js/**",
                "/images/**",
                "/layui/**"
                );
    }
    /**
     * http请求设置
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //http.csrf().disable(); //注释就是使用 csrf 功能
        http.headers().frameOptions().disable();//解决 in a frame because it set 'X-Frame-Options' to 'DENY' 问题
        //http.anonymous().disable();
        http.authorizeRequests()
            .antMatchers("/login/**","/initUserData","/main")//不拦截登录相关方法
            .permitAll()        
            //.antMatchers("/user").hasRole("ADMIN")  // user接口只有ADMIN角色的可以访问
//            .anyRequest()
//            .authenticated()// 任何尚未匹配的URL只需要验证用户即可访问
            .anyRequest()
            .access("@rbacPermission.hasPermission(request, authentication)")//根据账号权限访问
            .and()
            .formLogin()
            .loginPage("/")
            .loginPage("/login")   //登录请求页
            .loginProcessingUrl("/login")  //登录POST请求路径
            .usernameParameter("username") //登录用户名参数
            .passwordParameter("password") //登录密码参数
            .defaultSuccessUrl("/main")   //默认登录成功页面
            .and()
            .exceptionHandling()
            .accessDeniedHandler(customAccessDeniedHandler) //无权限处理器
            .and()
            .logout()
            .logoutSuccessUrl("/login?logout");  //退出登录成功URL

    }
    /**
     * 自定义获取用户信息接口
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 密码加密算法
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }
}
```
</details>

### 2.2 自定义实现 UserDetails 接口，扩展属性

<details>
<summary>查看代码</summary>

```java
public class UserEntity implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = -9005214545793249372L;

    private Long id;// 用户id
    private String username;// 用户名
    private String password;// 密码
    private List<Role> userRoles;// 用户权限集合
    private List<Menu> roleMenus;// 角色菜单集合

    private Collection<? extends GrantedAuthority> authorities;
    public UserEntity() {

    }

    public UserEntity(String username, String password, Collection<? extends GrantedAuthority> authorities,
            List<Menu> roleMenus) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.roleMenus = roleMenus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Menu> getRoleMenus() {
        return roleMenus;
    }

    public void setRoleMenus(List<Menu> roleMenus) {
        this.roleMenus = roleMenus;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
```
</details>


### 2.3 自定义实现 UserDetailsService 接口

<details>
<summary>查看代码</summary>

```java
/**
 * 获取用户相关信息
 * @author charlie
 *
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private MenuDao menuDao;

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查找用户
        UserEntity user = userDao.getUserByUsername(username);
        System.out.println(user);
        if (user != null) {
            System.out.println("UserDetailsService");
            //根据用户id获取用户角色
            List<Role> roles = roleDao.getUserRoleByUserId(user.getId());
            // 填充权限
            Collection<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            }
            //填充权限菜单
            List<Menu> menus=menuDao.getRoleMenuByRoles(roles);
            return new UserEntity(username,user.getPassword(),authorities,menus);
        } else {
            System.out.println(username +" not found");
            throw new UsernameNotFoundException(username +" not found");
        }        
    }

}
```
</details>


### 2.4 自定义实现 URL 权限控制

<details>
<summary>查看代码</summary>


```java
/**
 * RBAC数据模型控制权限
 * @author charlie
 *
 */
@Component("rbacPermission")
public class RbacPermission{

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        boolean hasPermission = false;
        // 读取用户所拥有的权限菜单
        List<Menu> menus = ((UserEntity) principal).getRoleMenus();
        System.out.println(menus.size());
        for (Menu menu : menus) {
            if (antPathMatcher.match(menu.getMenuUrl(), request.getRequestURI())) {
                hasPermission = true;
                break;
            }
        }
        return hasPermission;
    }
}
```
</details>


### 2.5 实现 AccessDeniedHandler

<details>
<summary>查看代码</summary>

```java
/**
 * 处理无权请求
 * @author charlie
 *
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        boolean isAjax = ControllerTools.isAjaxRequest(request);
        System.out.println("CustomAccessDeniedHandler handle");
        if (!response.isCommitted()) {
            if (isAjax) {
                String msg = accessDeniedException.getMessage();
                log.info("accessDeniedException.message:" + msg);
                String accessDenyMsg = "{\"code\":\"403\",\"msg\":\"没有权限\"}";
                ControllerTools.print(response, accessDenyMsg);
            } else {
                request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/403");
                dispatcher.forward(request, response);
            }
        }

    }

    public static class ControllerTools {
        public static boolean isAjaxRequest(HttpServletRequest request) {
            return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        }

        public static void print(HttpServletResponse response, String msg) throws IOException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(msg);
            writer.flush();
            writer.close();
        }
    }

}
```
</details>


### 2.6 相关 Controller

<details>
<summary>查看代码</summary>

```java
/**
 * 登录/退出跳转
 * @author charlie
 *
 */
@Controller
public class LoginController {
    @GetMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView mav = new ModelAndView();
        if (error != null) {
            mav.addObject("error", "用户名或者密码不正确");
        }
        if (logout != null) {
            mav.addObject("msg", "退出成功");
        }
        mav.setViewName("login");
        return mav;
    }
}

//登录成功跳转

@Controller
public class MainController {

    @GetMapping("/main")
    public ModelAndView toMainPage() {
        //获取登录的用户名
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username=null;
        if(principal instanceof UserDetails) {
            username=((UserDetails)principal).getUsername();
        }else {
            username=principal.toString();
        }
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main");
        mav.addObject("username", username);
        return mav;
    }

}
//用于不同权限页面访问测试

/**
 * 用于不同权限页面访问测试
 * @author charlie
 *
 */
@Controller
public class ResourceController {

    @GetMapping("/publicResource")
    public String toPublicResource() {
        return "resource/public";
    }

    @GetMapping("/vipResource")
    public String toVipResource() {
        return "resource/vip";
    }
}

//用于不同权限ajax请求测试

/**
 * 用于不同权限ajax请求测试
 * @author charlie
 *
 */
@RestController
@RequestMapping("/test")
public class HttptestController {

    @PostMapping("/public")
    public JSONObject doPublicHandler(Long id) {
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("msg", "请求成功" + id);
        return json;
    }

    @PostMapping("/vip")
    public JSONObject doVipHandler(Long id) {
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("msg", "请求成功" + id);
        return json;
    }
}
```
</details>


### 2.7 相关 html 页面

<details>
<summary>查看代码</summary>

```html
//登录页面

<form class="layui-form" action="/login" method="post">
            <div class="layui-input-inline">
                <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
                <input type="text" name="username" required
                    placeholder="用户名" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-input-inline">
                <input type="password" name="password" required  placeholder="密码" autocomplete="off"
                    class="layui-input">
            </div>
            <div class="layui-input-inline login-btn">
                <button id="btnLogin" lay-submit lay-filter="*" class="layui-btn">登录</button>
            </div>
            <div class="form-message">
                <label th:text="${error}"></label>
                <label th:text="${msg}"></label>
            </div>
        </form>

//防止跨站请求伪造（CSRF）攻击

//退出系统

<form id="logoutForm" action="/logout" method="post"
                                style="display: none;">
                                <input type="hidden" th:name="${_csrf.parameterName}"
                                    th:value="${_csrf.token}">
                            </form>
                            <a
                                href="javascript:document.getElementById('logoutForm').submit();">退出系统</a>

//ajax 请求页面

<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" id="hidCSRF">
<button class="layui-btn" id="btnPublic">公共权限请求按钮</button>
<br>
<br>
<button class="layui-btn" id="btnVip">VIP权限请求按钮</button>
<script type="text/javascript" th:src="@{/js/jquery-1.8.3.min.js}"></script>
<script type="text/javascript" th:src="@{/layui/layui.js}"></script>
<script type="text/javascript">
        layui.use('form', function() {
            var form = layui.form;
            $("#btnPublic").click(function(){
                $.ajax({
                    url:"/test/public",
                    type:"POST",
                    data:{id:1},
                    beforeSend:function(xhr){
                        xhr.setRequestHeader('X-CSRF-TOKEN',$("#hidCSRF").val());    
                    },
                    success:function(res){
                        alert(res.code+":"+res.msg);

                    }    
                });
            });
            $("#btnVip").click(function(){
                $.ajax({
                    url:"/test/vip",
                    type:"POST",
                    data:{id:2},
                    beforeSend:function(xhr){
                        xhr.setRequestHeader('X-CSRF-TOKEN',$("#hidCSRF").val());    
                    },
                    success:function(res){
                        alert(res.code+":"+res.msg);

                    }
                });
            });
        });
    </script>
```
</details>



