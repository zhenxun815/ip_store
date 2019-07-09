package com.tqhy.ip_store.controllers;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.services.RawDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@RestController
@RequestMapping(path = "/raw-docs")
public class RawDocController {

    @Autowired
    RawDocService rawDocService;

    @GetMapping("/")
    public List<RawDoc> getAll() {
        return rawDocService.findAll().orElse(new ArrayList<>());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RawDoc getById(@PathVariable(name = "id")  String id) {
        return rawDocService.findById(id).orElse(null);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public RawDoc getByDocId(@RequestParam(name = "appId") String appId,
                             @RequestParam(name = "pubId", required = false) String pubId) {
        Optional<RawDoc> byAppIdOpt = rawDocService.findByAppId(appId);
        if (byAppIdOpt.isPresent()) {
            if (StringUtils.isEmpty(pubId)) {
                return byAppIdOpt.get();
            }

            Optional<RawDoc> byPubIdOpt = rawDocService.findByPubId(pubId);
            if (byPubIdOpt.isPresent()) {
                RawDoc byAppId = byAppIdOpt.get();
                RawDoc byPubId = byPubIdOpt.get();
                if (byAppId.get_id().equals(byPubId.get_id())) {
                    return byAppId;
                }
            }
            return null;
        }
        return null;
    }
}
