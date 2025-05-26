package passwordmanager.controllers;

import passwordmanager.DAO.ServicesDAO;

public class PasswordGeneratorController {
    private ServicesDAO servicesDAO;

    public void setServicesDAO(ServicesDAO servicesDAO) {
        this.servicesDAO = servicesDAO;
    }

}
