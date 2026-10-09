package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Address;
import com.campus.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(addressService.list());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Address address) {
        return addressService.add(address);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Address address) {
        return addressService.update(address);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return addressService.delete(id);
    }

    @PostMapping("/default")
    public Result<?> setDefault(@RequestBody java.util.Map<String, Integer> body) {
        return addressService.setDefault(body.get("id"));
    }
}
