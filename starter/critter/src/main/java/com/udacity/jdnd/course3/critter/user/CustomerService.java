package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer=convertCustomerDTOToEntity(customerDTO);
        customer=customerRepository.save(customer);
        return convertEntityToCustomerDTO(customer);
    }

    private CustomerDTO convertEntityToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        if(customer.getPets() != null){
            List<Long> petIds = new ArrayList<>();
            customer.getPets().forEach(pet -> petIds.add(pet.getId()));
            customerDTO.setPetIds(petIds);
        }
        return customerDTO;
    }

    private Customer convertCustomerDTOToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        if(customerDTO.getPetIds() != null){
            List<Pet> pets = new ArrayList<>();
            customerDTO.getPetIds().forEach(petId -> pets.add(petRepository.findById(petId).get()));
            customer.setPets(pets);
        }
        return customer;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customerList=customerRepository.findAll();
        if(customerList != null){
            List<CustomerDTO> customerDTOList=new ArrayList<>();
            for(Customer customer :customerList){
                customerDTOList.add(convertEntityToCustomerDTO(customer));
            }
            return customerDTOList;
        }
        else {
            throw new NullPointerException();
        }
    }

    public CustomerDTO getOwnerByPet(long petId) {
        Optional<Pet> petOptional= petRepository.findById(petId);
        if(petOptional.isPresent()){
            Optional<Customer> customerOptional=customerRepository.findById(petOptional.get().getCustomer().getId());
            if(customerOptional.isPresent()){
                return convertEntityToCustomerDTO(customerOptional.get());
            }else{
                throw new NullPointerException();
            }
        }else {
            throw new NullPointerException();
        }
    }
}
