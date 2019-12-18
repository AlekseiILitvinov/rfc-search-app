package ru.itpark.webapp.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import ru.itpark.webapp.repository.RfcRepository;
import ru.itpark.webapp.repository.RfcRepositoryImpl;
import ru.itpark.webapp.service.FileService;
import ru.itpark.webapp.service.FileServiceImpl;
import ru.itpark.webapp.servlet.MainServlet;
import ru.itpark.webapp.controller.RfcController;
import ru.itpark.webapp.service.RfcService;
import ru.itpark.webapp.controller.RfcControllerImpl;
import ru.itpark.webapp.service.RfcServiceImpl;

public class ServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/", "/page", "/search/*", "/search", "/results", "/remove/*", "/remove/", "/populateASAP").with(MainServlet.class);
                bind(RfcController.class).to(RfcControllerImpl.class);
                bind(RfcService.class).to(RfcServiceImpl.class);
                bind(FileService.class).to(FileServiceImpl.class);
                bind(RfcRepository.class).to(RfcRepositoryImpl.class);
            }
        });
    }
}
