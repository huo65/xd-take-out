package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huo.common.BaseContext;
import com.huo.common.CustomException;
import com.huo.common.GeneralResult;
import com.huo.entity.AddressBook;
import com.huo.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增收获地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public GeneralResult<AddressBook> save(@RequestBody  AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info(addressBook.toString());
        addressBookService.save(addressBook);
        return GeneralResult.success(addressBook);
    }

    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public GeneralResult<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}",addressBook);
        addressBook.setUserId(BaseContext.getCurrentId());
        //TODO 学习Mybatis-plus
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
//        log.info("look here@@@@@@"+addressBook.getIsDefault().toString());
        return GeneralResult.success(addressBook);
    }

    /**
     * 查看收货地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public GeneralResult<List<AddressBook> > list (AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> addressBooks= addressBookService.list(queryWrapper);
        return GeneralResult.success(addressBooks);
    }

    /**
     * 查询默认地址
     * @return
     */

    @GetMapping("/default")
     public GeneralResult<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        log.info("&&&&&&&&&&&&&&&&&&&&&查询地址ing&&&&&&&&&&&&&&&&&&&&&&&&&");
        if (addressBook == null) {
            return GeneralResult.error("没有找到默认地址");
        }else{
            return GeneralResult.success(addressBook);
        }
     }

    @GetMapping("/{id}")
    public GeneralResult<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null){
            throw new CustomException("地址信息不存在");
        }
        return GeneralResult.success(addressBook);
    }
    @PutMapping
    public GeneralResult<String> updateAdd(@RequestBody AddressBook addressBook) {
        if (addressBook == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        addressBookService.updateById(addressBook);
        return GeneralResult.success("地址修改成功");
    }

    @DeleteMapping()
    public GeneralResult<String> deleteAdd(@RequestParam("ids") Long id) {
        if (id == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        addressBookService.removeById(id);
        return GeneralResult.success("地址删除成功");
    }
}
