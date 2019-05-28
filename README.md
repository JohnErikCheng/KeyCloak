## KeyCloak的使用：
### 一、keycloak在linux上的搭建
### 二、keycloak的界面设置
1、访问地址：
http://192.168.56.100:8080/auth/

2、点击 Administration Console
输入用户名、密码，登录进界面

3、创建realm
Keycloak 定义了一个 realm 的概念，并且你将在 realm 中定义客户端，
在 Keycloak 中的术语是指由 Keycloak 保护的应用程序。 它可以是 Web App，Java EE 后端，Spring Boot 等等。
所以让我们创建一个新的 realm，只需点击“Add realm”按钮：输入一个新增的realm名称，点击保存

4、创建客户端
现在我们需要定义一个客户端，这就会是我们的 Spring Boot 应用程序。
转到“Clients”部分，然后单击“Create”按钮。我们把这个客户端叫做“spring-keycloak-app”：
设置Valid Redirect URIs：http://localhost:8086/*
设置Base URL：http://localhost:8086

5、创建用户并分配角色
创建两个角色role:分别是test-role和user-role
创建两个用户user:分别是aaa和bbb
分别赋予不同的用户不同的角色：aaa--->user-role,bbb--->test-role
分别设置不同用户密码

### 三、springboot项目的创建
1、创建springboot项目
2、添加pom依赖
```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- keycloak-spring-boot-starter -->
        <dependency>
            <groupId>org.keycloak</groupId>
            <artifactId>keycloak-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- 引入Keycloak adapter的bom-->
            <dependency>
                <groupId>org.keycloak.bom</groupId>
                <artifactId>keycloak-adapter-bom</artifactId>
                <version>6.0.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```
3、yaml文件的设置
```yaml
    keycloak:
      # 表示是一个public的client
      public-client: true
      # keycloak的地址
      auth-server-url: http://192.168.56.100:8080/auth
      # keycloak中的realm
      realm: realm-test
      resource: spring-keycloak-app
      securityConstraints:
        - authRoles:
            # 以下路径需要user-role角色才能访问
            - test-role
          securityCollections:
            # name可以随便写
            - name: test-role-mappings
              patterns:
                - /articles/*
    server:
      port: 8086
```
4、编写controller
```java
@Controller
public class AuthController {
    @GetMapping("/articles")
    public String search(Principal principal, Model model) {
        if (principal instanceof KeycloakPrincipal) {
            AccessToken accessToken = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken();
            String userName = accessToken.getPreferredUsername();
            AccessToken.Access realmAccess = accessToken.getRealmAccess();
            Set<String> roles = realmAccess.getRoles();
            System.out.println("当前登录用户:" + userName + ",角色：" + roles);
            model.addAttribute("userName",userName);
            model.addAttribute("roles",roles);
        }
        return "index";
    }
}
```
### 四、测试项目
1、分别使用不同的用户（即使用不同的用户）进行访问 http://localhost:8086

2、进行跳转到192.168.56.100/auth

3、验证用户

    - 与yaml文件中设置的权限比较
    - 匹配，成功访问
    - 不匹配，访问失败