package com.roudane.commerce.order.infrastructure.rest.order.mapper;

import com.roudane.commerce.order.application.command.CreateOrderCommand;
import com.roudane.commerce.order.infrastructure.rest.order.dto.CreateOrderRequest;
import com.roudane.commerce.order.infrastructure.rest.order.dto.OrderLineRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderRequestMapper {

    OrderRequestMapper INSTANCE = Mappers.getMapper(OrderRequestMapper.class);

    // MapStruct mappe automatiquement les collections (List<OrderLineRequest> -> List<OrderLineCommand>)
    CreateOrderCommand toCommand(CreateOrderRequest request);

    // Mappage de la ligne de commande avec renommage du champ 'threshold' -> 'discountThreshold'
    @Mapping(target = "discountThreshold", source = "threshold")
    CreateOrderCommand.OrderLineCommand toOrderLineCommand(OrderLineRequest request);

    // Mappage automatique des Enums ayant les mêmes noms de constantes
    CreateOrderCommand.DiscountType toDiscountType(OrderLineRequest.DiscountType discountType);
}