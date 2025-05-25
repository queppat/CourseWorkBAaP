package passwordmanager.controllers;

import passwordmanager.DAO.ServicesDAO;

public class PasswordGeneratorTabController {
    private MainController mainController;
    private ServicesDAO servicesDAO;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setServicesDAO(ServicesDAO servicesDAO) {
        this.servicesDAO = servicesDAO;
    }

}
