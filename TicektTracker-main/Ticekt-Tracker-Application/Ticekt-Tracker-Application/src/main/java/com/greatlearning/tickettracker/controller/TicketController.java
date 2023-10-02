package com.greatlearning.tickettracker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.greatlearning.tickettracker.entity.Ticket;
import com.greatlearning.tickettracker.repository.TicketRepository;

@Controller
public class TicketController {

    @Autowired
    TicketRepository ticketRepository;

    TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/admin/tickets")
    public String getAllTickets(Model model) {
        List<Ticket> ticketList = (List<Ticket>) this.ticketRepository.findAll();
        model.addAttribute("tickets_list", ticketList);
        return "index";
    }

    @GetMapping("/admin/tickets/{id}/edit")
    public String editTicket(Model model, @PathVariable("id") Integer id) {
       Optional<Ticket> editOptionalTicket = this.ticketRepository.findById(id);
        Ticket editTicket = editOptionalTicket.get();
        model.addAttribute("edit_ticket", editTicket);
        return "edit";
    }

    @GetMapping("/admin/tickets/newTicket")
    public String createNewTicket(Model model) {
        Ticket newTicket = new Ticket();
        model.addAttribute("newTicket", newTicket);
        return "create";
    }

    @GetMapping("/admin/tickets/delete/{id}")
    public String deleteTicket(Model model, @PathVariable("id") Integer id) {
        Optional<Ticket> deleteOptionalTicket = this.ticketRepository.findById(id);
        Ticket deletedTicket = deleteOptionalTicket.get();
        this.ticketRepository.delete(deletedTicket);
        return "redirect:/admin/tickets";
    }

    @GetMapping("/admin/tickets/view/{id}")
    public String getTicketView(Model model, @PathVariable("id") Integer id) {
        Optional<Ticket> getOptionalTicket = this.ticketRepository.findById(id);
        Ticket ticketView = getOptionalTicket.get();
        model.addAttribute("ticket_view", ticketView);
        return "view";
    }

    @RequestMapping(path = {"/","/admin/tickets/search"})
    public String searchResults(Model model, @Param("keyword") String keyword) {

        List<Ticket> ticketList = (List<Ticket>) this.ticketRepository.findAll();
        if(keyword == null) {
            model.addAttribute("tickets_list", ticketList);
            return "index";
        }
        List<Ticket> searchResults = this.ticketRepository.getTicketsByKeyWord(keyword);
        model.addAttribute("tickets_list", searchResults );
        return "index";
    }
    @PostMapping("/admin/ticket/save")
    public String saveNewTicket(@ModelAttribute("ticket") Ticket ticket) {

        this.ticketRepository.save(ticket);
        return "redirect:/admin/tickets";
    }

//    @RequestMapping(
//            path = "/admin/tickets/{id}/edit",
//            method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = {
//                    MediaType.APPLICATION_ATOM_XML_VALUE,
//                    MediaType.APPLICATION_JSON_VALUE
//            })
    @PostMapping(value = "/admin/tickets/{id}/edit")
    public String editTicket(@PathVariable("id") Integer id, @ModelAttribute("ticket") Ticket ticket) {

        Optional<Ticket> editOptionalTicket = this.ticketRepository.findById(id);

        if(!editOptionalTicket.isPresent())
            return null;

        Ticket oldTicket = editOptionalTicket.get();

        if(ticket.getTitle() != null)
            oldTicket.setTitle(ticket.getTitle());

        if(ticket.getContent() != null)
            oldTicket.setContent(ticket.getContent());

        if(ticket.getDescription() != null)
            oldTicket.setDescription(ticket.getDescription());

        Ticket updatedTicket = oldTicket;
        this.ticketRepository.save(updatedTicket);
        return "redirect:/admin/tickets";
    }
}