package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.Result;
import com.campus.entity.Address;
import com.campus.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> list() {
        AuthContext ctx = AuthContext.get();
        return addressMapper.findByUserId(ctx.getUserId());
    }

    public Result<?> add(Address address) {
        AuthContext ctx = AuthContext.get();
        address.setUserId(ctx.getUserId());
        if (address.getBuilding() == null || address.getBuilding().isEmpty()) {
            return Result.error("楼栋不能为空");
        }
        if (address.getDorm() == null || address.getDorm().isEmpty()) {
            return Result.error("宿舍号不能为空");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(ctx.getUserId());
        }
        addressMapper.insert(address);
        return Result.success(address);
    }

    public Result<?> update(Address address) {
        AuthContext ctx = AuthContext.get();
        Address exist = addressMapper.findById(address.getId());
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error("地址不存在");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(ctx.getUserId());
        }
        addressMapper.update(address);
        return Result.success();
    }

    public Result<?> delete(Integer id) {
        AuthContext ctx = AuthContext.get();
        Address exist = addressMapper.findById(id);
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error("地址不存在");
        }
        addressMapper.delete(id);
        return Result.success();
    }

    public Result<?> setDefault(Integer id) {
        AuthContext ctx = AuthContext.get();
        Address exist = addressMapper.findById(id);
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error("地址不存在");
        }
        addressMapper.clearDefault(ctx.getUserId());
        addressMapper.setDefault(id);
        return Result.success();
    }
}
