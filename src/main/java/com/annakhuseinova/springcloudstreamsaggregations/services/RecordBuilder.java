package com.annakhuseinova.springcloudstreamsaggregations.services;

import com.annakhuseinova.springcloudstreamsaggregations.model.DepartmentAggregate;
import com.annakhuseinova.springcloudstreamsaggregations.model.Employee;
import com.annakhuseinova.springcloudstreamsaggregations.model.Notification;
import com.annakhuseinova.springcloudstreamsaggregations.model.PosInvoice;
import org.springframework.stereotype.Service;

@Service
public class RecordBuilder {

    public Notification getNotification(PosInvoice invoice){
        Notification notification = new Notification();
        notification.setInvoiceNumber(invoice.getInvoiceNumber());
        notification.setCustomerCardNo(invoice.getCustomerCardNo());
        notification.setTotalAmount(invoice.getTotalAmount());
        notification.setEarnedLoyaltyPoints(invoice.getTotalAmount() * 0.02);
        notification.setTotalLoyaltyPoints(notification.getEarnedLoyaltyPoints());
        return notification;
    }

    public DepartmentAggregate init(){
        DepartmentAggregate departmentAggregate = new DepartmentAggregate();
        departmentAggregate.setEmployeeCount(0);
        departmentAggregate.setTotalSalary(0);
        departmentAggregate.setAvgSalary(0D);
        return departmentAggregate;
    }

    public DepartmentAggregate aggregate(Employee employee, DepartmentAggregate aggValue){
        DepartmentAggregate departmentAggregate = new DepartmentAggregate();
        departmentAggregate.setEmployeeCount(aggValue.getEmployeeCount() + 1);
        departmentAggregate.setAvgSalary(aggValue.getTotalSalary() + employee.getSalary() / aggValue.getEmployeeCount() + 1D);
        departmentAggregate.setTotalSalary(aggValue.getTotalSalary() + employee.getSalary());
        return departmentAggregate;

    }

    public DepartmentAggregate subtract(Employee employee, DepartmentAggregate aggValue){
        DepartmentAggregate departmentAggregate = new DepartmentAggregate();
        departmentAggregate.setEmployeeCount(aggValue.getEmployeeCount()-1);
        departmentAggregate.setTotalSalary(aggValue.getTotalSalary() - employee.getSalary());
        departmentAggregate.setAvgSalary(aggValue.getTotalSalary() - employee.getSalary() / (aggValue.getEmployeeCount() - 1D));
        return departmentAggregate;
    }


}
