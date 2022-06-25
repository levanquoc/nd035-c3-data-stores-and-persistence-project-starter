package com.udacity.jdnd.course3.critter.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee=ConvertEmployeeDTOToEntity(employeeDTO);
        employee=employeeRepository.save(employee);
        return ConvertEntityToEmployeeDTO(employee);
    }

    private EmployeeDTO ConvertEntityToEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee,employeeDTO);
        return employeeDTO;
    }

    private Employee ConvertEmployeeDTOToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    public EmployeeDTO getEmployee(long employeeId) {
       Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
       if(employeeOptional.isPresent()){
           return ConvertEntityToEmployeeDTO(employeeOptional.get());
       }else {
           throw new NullPointerException();
       }
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
        if(employeeOptional.isPresent()){
            Employee employee=employeeOptional.get();
            employee.setDaysAvailable(daysAvailable);
            employeeRepository.save(employee);
        }else{
            throw new NullPointerException();
        }
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        Set<EmployeeSkill> employeeSkills = employeeDTO.getSkills();
        DayOfWeek availability = employeeDTO.getDate().getDayOfWeek();
        List<Employee> employees = employeeRepository.findAllByDaysAvailable(availability);
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();
        for(Employee employee : employees){
            if(employee.getSkills().containsAll(employeeSkills)){
                employeeDTOs.add(ConvertEntityToEmployeeDTO(employee));
            }
        }
        return employeeDTOs;
    }
}
