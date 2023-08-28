package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner")
public class PartnerController {

    @Autowired
    public PartnerRepository partnerRepository;

    @PostMapping(value = "/add")
    public void addPartner(@RequestBody Partner partner) {
        partnerRepository.save(partner);
    }

}
