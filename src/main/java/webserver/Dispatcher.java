package webserver;

import common.http.HttpRequest;
import common.http.HttpResponse;
import exception.NoSuchControllerMethodException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.View;

import java.lang.reflect.Method;

import static webserver.ServerConfig.ERROR_PAGE;

/**
 * Dispatcher의 역할
 * <li>요청을 처리할 수 있는 컨트롤러를 찾는다.</li>
 * <li>처리할 수 있는 컨트롤러가 있으면 처리 후 ModelView를 반환 받는다.</li>
 * <li>ViewResolver를 통해서 반환 받은 ModelView의 viewName을 실제 물리적 경로로 변경하여 뷰를 반환 받는다..</li>
 * <li>뷰를 렌더링 한다.</li>
 */
public class Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
    private static final Controller controller = new Controller();

    public void dispatch(HttpRequest request, HttpResponse response) throws Exception {
        ControllerMapper mapper = ControllerMapper.getInstance();

        ModelView mv = null;
        try {
            Method controllerMethod = mapper.getControllerMethod(request.getRequestMethod(), request.getRequestPath());
            mv = (ModelView) controllerMethod.invoke(controller, request, response);

        } catch (NoSuchControllerMethodException | IllegalArgumentException e) {
            mv = new ModelView(ERROR_PAGE);
            logger.error(e.getMessage());
        } finally {
            if (mv != null) {
                View view = ViewResolver.resolveViewName(mv.getViewName());
                view.render(mv.getModel(), request, response);
            }
        }
    }

}
