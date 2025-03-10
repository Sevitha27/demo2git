package com.example.demo2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeliveryPartner {
    private String partnerId;
    private List<String> assignedOrders = new ArrayList<>();
}
