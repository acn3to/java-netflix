import entities.*;
import repositories.MediaRepository;
import repositories.impl.*;
import services.LoginService;
import services.MediaService;
import services.UserService;

public class Main {
    public static void main(String[] args) {

        MediaRepositoryImpl mediaRepository = new MediaRepositoryImpl();
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        MediaService mediaService = new MediaService(mediaRepository);
        LoginService loginService = new LoginService(userService);


        NetflixApp netflixApp = new NetflixApp(userService, mediaService, loginService);

        userService.addUser(new User("Felipe","f_g@outlook.com.br","12345678",false));
        userService.addUser(new User("root","root@outlook.com.br","root12345678",true));

        netflixApp.run();


    }
}