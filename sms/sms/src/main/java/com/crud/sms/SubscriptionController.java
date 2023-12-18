package com.crud.sms;

import jdk.jshell.JShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @PostMapping
    public SubscriptionPlan createSubscription(@RequestBody SubscriptionRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByPlanName(request.getPlanName());

        if (user == null) {
            throw new UserNotFoundException(request.getEmail());
        }

        if (subscriptionPlan == null) {
            throw new SubscriptionPlanNotFoundException(request.getPlanName());
        }

        SubscriptionPlan subscription = new SubscriptionPlan();
        subscription.setUser(user);
        subscription.setSubscriptionPlan(subscriptionPlan);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(subscriptionPlan.getDuration()));
        subscription.setAmount(subscriptionPlan.getAmount());

        return subscription;
    }

    @GetMapping("/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return user.getSubscriptions();
    }

    @PutMapping("/{subscriptionId}/pay")
    public void updateSubscriptionPaymentStatus(@PathVariable Long subscriptionId) {
        Subscription subscription = findSubscriptionById(subscriptionId);
        subscription.setPaid(true);
    }

    @DeleteMapping("/{subscriptionId}")
    public void cancelSubscription(@PathVariable Long subscriptionId) {
        Subscription subscription = findSubscriptionById(subscriptionId);
        subscription.setCancelled(true);
    }

    private Subscription findSubscriptionById(Long subscriptionId)

    {
        return subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
    }
}
