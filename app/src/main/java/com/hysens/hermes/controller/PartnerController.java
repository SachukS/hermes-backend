package com.hysens.hermes.controller;

import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/partner")
public class PartnerController {

    @Autowired
    public PartnerRepository partnerRepository;

    @PostMapping(value = "/add")
    public void addPartner(@RequestBody Partner partner) {
        Partner existPartner;
        Optional<Partner> partnerFromDb = partnerRepository.findById(Long.parseLong("1"));
        if (partnerFromDb.isPresent())
            existPartner = partnerFromDb.get();
        else {
            existPartner = new Partner();
            existPartner.setId(1);
        }
        existPartner.setModifyDateTime(LocalDateTime.now());
        existPartner.setEmail(partner.getEmail());
        existPartner.setExecutionTime(partner.getExecutionTime());
        existPartner.setName(partner.getName());
        existPartner.setPhone(partner.getPhone());
        existPartner.setResponseTime(partner.getResponseTime());
        existPartner.setResponseNotification(partner.getResponseNotification());

        partnerRepository.save(existPartner);
    }

    @GetMapping(value = "/get/{id}")
    public Partner getPartner(@PathVariable("id") long id) {
        return partnerRepository.findById(id).orElse(new Partner());
    }
}
