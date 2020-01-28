package menu;

/**<h1>User Controller</h1>
 * Implementing this enables you to initialize data in the newly opened window that would not be
 * possible in the controllers constructor due to how JavaFX functions.
 * </br>
 * Simply put all of the objects you want in the new window in the var-arg, and then
 * initialize them in the new controller. You will obviously have to keep track of the order and cast them accordingly.
 */
public interface UserController {

    /**
     * Used to initialize objects in a different controller.
     * Keep track of the order and cast them accordingly.
     * @param obj All the objects you want in the controller.
     */
    void initData(Object ...obj);
}
