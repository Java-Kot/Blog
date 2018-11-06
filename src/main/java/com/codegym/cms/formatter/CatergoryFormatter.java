package com.codegym.cms.formatter;

import com.codegym.cms.model.Catergory;
import com.codegym.cms.service.CatergoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class CatergoryFormatter implements Formatter<Catergory> {

    private CatergoryService catergoryService;

    @Autowired
    public CatergoryFormatter(CatergoryService catergoryService) {
        this.catergoryService = catergoryService;
    }

    @Override
    public Catergory parse(String text, Locale locale) throws ParseException {
        return catergoryService.findById(Long.parseLong(text));
    }

    @Override
    public String print(Catergory object, Locale locale) {
        return "[" + object.getId() + ", " +object.getName() + "]";
    }
}
