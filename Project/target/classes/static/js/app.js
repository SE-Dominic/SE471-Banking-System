function checkSessionStatus() {
    fetch('/api/user/sessionStatus', {
        method: 'GET',
        credentials: 'include' // Include cookies in the request
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Session validation failed');
        }
        return response.json();
    })
    .then(data => {
        if (data.isLoggedIn) {
            alert('User is logged in as: ' + data.username);
        } else {
            console.log('User is not logged in');
            window.location.hash = 'login'; // Redirect to login page
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function callLogInUserEndpoint(button) {
    console.log("login endpoint called");
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    // Validate inputs
    if (!username || !password) {
        alert("Please enter both username and password");
        return;
    }

    // Include username and password in the query string
    fetch(`/api/user/logInUser?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to log in');
        }
        return response.json();
    })
    .then(isLoggedIn => {
        if (isLoggedIn) {
            console.log("Login successful, navigating to dashboard...");
            window.location.hash = 'dashboard';
        } else {
            console.log("Login failed, staying on the current page.");
            alert("Incorrect username or password. Please try again.");
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("An error occurred while trying to log in. Please try again later.");
    });
}

function callLogOutUserEndpoint() {
    console.log("Logout endpoint called");

    fetch('/api/user/logOutUser', {
        method: 'GET',
        credentials: 'include' // Include cookies in the request
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to log out');
        }
        console.log("Logout successful, redirecting to login page...");
        window.location.hash = 'login'; // Redirect to the login page
    })
    .catch(error => {
        console.error('Error:', error);
        alert("An error occurred while trying to log out. Please try again later.");
    });
}

function callCreateNewUserEndpoint() {
    // Get all form values
    const username = document.getElementById('signup-username').value;
    const password = document.getElementById('signup-password').value;
    const firstName = document.getElementById('signup-firstname').value;
    const lastName = document.getElementById('signup-lastname').value;
    const email = document.getElementById('signup-email').value;
    const phoneNum = document.getElementById('signup-phone-number').value;
    const socialSecurityNum = document.getElementById('signup-ssn').value;
    const street = document.getElementById('signup-street').value;
    const city = document.getElementById('signup-city').value;
    const state = document.getElementById('signup-state').value;
    const zip = document.getElementById('signup-zip').value;

    // Create query string with all parameters
    const params = new URLSearchParams({
        username,
        password,
        firstName,
        lastName,
        email,
        phoneNum: parseInt(phoneNum.replace(/\D/g, '')), // Remove non-digits and parse as integer
        socialSecurityNum: parseInt(socialSecurityNum.replace(/\D/g, '')), // Remove non-digits and parse as integer
        street,
        city,
        state,
        zip
    });

    fetch(`/api/user/createNewUser?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to create user');
        }
        console.log('User created successfully');
        window.location.hash = 'login'; // Redirect to login page
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while creating the user. Please try again.');
    });
}

function callCreateBankAccountEndpoint() {
    const accountName = document.getElementById('account-name').value;
    const accountType = document.getElementById('account-type').value;
    const initialDeposit = document.getElementById('initial-deposit').value;

    if (!accountName || !accountType || !initialDeposit) {
        alert('Please fill out all fields.');
        return;
    }

    fetch('/api/user/sessionStatus', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to get session status');
        }
        return response.json();
    })
    .then(sessionData => {
        if (!sessionData.isLoggedIn) {
            throw new Error('User not logged in');
        }

        const params = new URLSearchParams({
            accountName,
            accountType,
            initialDeposit,
            userID: sessionData.username
        });

        return fetch(`/api/bankAccount/createBankAccount?${params.toString()}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to create bank account');
        }
        alert('Bank account created successfully!');
        window.location.hash = 'accounts';
        getAllConnectedBankAccountsEndpoint();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while creating the bank account. Please try again.');
    });
}

function getAllConnectedBankAccountsEndpoint() {
    // Show loading state
    const accountStack = document.getElementById('connected-account-stack');
    accountStack.innerHTML = '<div class="loading-state"><p>Loading your accounts...</p></div>';
    
    // First fetch: Get user session data
    fetch('/api/user/sessionStatus', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to get session status');
        }
        return response.json();
    })
    .then(sessionData => {
        if (!sessionData.isLoggedIn) {
            throw new Error('User not logged in');
        }
        
        // Second fetch: Get connected bank accounts using the username
        return fetch(`/api/bankAccount/getConnectedBankAccounts?targetConnectedUserID=cU${sessionData.username}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch connected bank accounts');
        }
        return response.json();
    })
    .then(bankAccounts => {
        // Clear the loading state
        accountStack.innerHTML = '';
        
        if (bankAccounts.length === 0) {
            accountStack.innerHTML = `
                <div class="empty-state">
                    <p>No connected bank accounts found.</p>
                    <button class="btn" onclick="getAllConnectedBankAccountsEndpoint()">Try Again</button>
                </div>
            `;
            return;
        }
        
        // Create a new list with enhanced styling
        const ul = document.createElement('ul');
        ul.className = 'account-list';
        
        bankAccounts.forEach(bankAccount => {
            const li = document.createElement('li');
            li.className = 'account-content';
            li.innerHTML = generateBankAccountHTML(bankAccount);
            ul.appendChild(li);
        });
        
        accountStack.appendChild(ul);
    })
    .catch(error => {
        console.error('Error:', error);
        accountStack.innerHTML = `
            <div class="empty-state">
                <p>An error occurred while fetching your accounts.</p>
                <button class="btn" onclick="getAllConnectedBankAccountsEndpoint()">Try Again</button>
            </div>
        `;
    });
}
  
// Initialize when the page loads
document.addEventListener('DOMContentLoaded', function() {

});

function generateBankAccountHTML(bankAccount) {
    return `
        <div class="account-content">
            <div class="account-details">
                <div class="bank-account-name">Bank Account: ${bankAccount.bankAccountName}</div>
                <div class="bank-account-balance">Current Balance: $${bankAccount.balance.toFixed(2)}</div>
            </div>
            <div class="bank-account-actions">
                <button class="action-btn" onclick="redirectToDepositForm('${bankAccount.bankAccountID}')">Deposit Funds</button>
                <button class="action-btn" onclick="redirectToWithdrawForm('${bankAccount.bankAccountID}')">Withdraw Funds</button>
                <button class="action-btn" onclick="redirectTransactionForm('${bankAccount.bankAccountID}')">Transfer Funds</button>
                <button class="action-btn" onclick="redirectTransactions('${bankAccount.bankAccountID}')">View Transaction History</button>
            </div>
        </div>
    `;
}

function loadProfileInfo() {
    const profileDiv = document.getElementById('load-profile-info');
    profileDiv.innerHTML = '<p>Loading profile...</p>';

    fetch('/api/user/sessionStatus', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to get session status');
        }
        return response.json();
    })
    .then(data => {
        console.log('Session data:', data); // Debugging log

        if (!data.isLoggedIn) {
            window.location.hash = 'login';
            return;
        }

        profileDiv.innerHTML = `
            <div class="profile-card">
                <div class="profile-row"><span class="profile-label">User ID:</span><span class="profile-value">${data.userID || 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">Username:</span><span class="profile-value">${data.username || 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">Name:</span><span class="profile-value">${data.name || 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">Email:</span><span class="profile-value">${data.email || 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">Phone:</span><span class="profile-value">${data.phoneNum || 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">SSN:</span><span class="profile-value">***-**-${data.socialSecurityNum ? String(data.socialSecurityNum).slice(-4) : 'N/A'}</span></div>
                <div class="profile-row"><span class="profile-label">Address:</span><span class="profile-value">${data.address || 'N/A'}</span></div>
            </div>
        `;
    })
    .catch(error => {
        console.error('Error loading profile:', error);
        profileDiv.innerHTML = '<p>Error loading profile. Please try again.</p>';
    });
}

function redirectTransactionForm(bankAccountID) {
    // Redirect to the transaction form page with the bank account ID as a query parameter
    window.location.hash = `transactionForm.html?bankAccountID=${encodeURIComponent(bankAccountID)}`;
}
function getDepositAmount() {
    const urlParams = new URLSearchParams(window.location.hash.split('?')[1]);
    const bankAccountID = urlParams.get('bankAccountID');


    const depositAmount = parseFloat(document.getElementById('depositAmount').value);
    
    if (!depositAmount || depositAmount <= 0) {
        alert("Please enter a valid deposit amount.");
        return;
    }

    fetch(`/api/bankAccount/depositFunds?bankAccountID=${encodeURIComponent(bankAccountID)}&amount=${depositAmount}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
            bankAccountID: bankAccountID, 
            amount: depositAmount })  
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Deposit failed');
        }
        return response.json();
    })
    .then(success => {
        if (success) {
            alert('Deposit completed successfully!');
            window.location.hash = 'accounts'; // Redirect back to accounts page
        } else {
            alert('Deposit failed. Please try again.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while processing the deposit. Please try again.');
    });
}
function getWithdrawAmount() {
    const urlParams = new URLSearchParams(window.location.hash.split('?')[1]);
    const bankAccountID = urlParams.get('bankAccountID');
    const withdrawAmount = parseFloat(document.getElementById('withdrawAmount').value);
    
    if (!withdrawAmount || withdrawAmount <= 0) {
        alert("Please enter a valid withdrawal amount.");
        return;
    }

    fetch(`/api/bankAccount/withdrawFunds?bankAccountID=${encodeURIComponent(bankAccountID)}&amount=${withdrawAmount}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
            bankAccountID: bankAccountID, 
            amount: withdrawAmount })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Withdrawal failed');
        }
        return response.json();
    })
    .then(success => {
        if (success) {
            alert('Withdrawal completed successfully!');
            window.location.hash = 'accounts'; // Redirect back to accounts page
        } else {
            alert('Withdrawal failed. Please check your balance and try again.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while processing the withdrawal. Please try again.');
    });
}
function redirectToDepositForm(bankAccountID) {
    // Redirect to the deposit form page with the bank account ID as a query parameter
    window.location.hash = `depositForm.html?bankAccountID=${encodeURIComponent(bankAccountID)}`;
}

function redirectToWithdrawForm(bankAccountID) {
    // Redirect to the withdraw form page with the bank account ID as a query parameter
    window.location.hash = `withdrawForm.html?bankAccountID=${encodeURIComponent(bankAccountID)}`;
}
function redirectTransactions(bankAccountID) {
    // Redirect to the withdraw form page with the bank account ID as a query parameter
    window.location.hash = `transactions.html?bankAccountID=${encodeURIComponent(bankAccountID)}`;
}
// Update the function signature to accept it
function populateAccountDropdown(currentAccountID) {
    // Remove the URL parsing - you already have what you need
    // const urlParams = new URLSearchParams(window.location.hash.split('?')[1]); ❌ delete this
    // const currentAccountID = urlParams.get('bankAccountID');                   ❌ delete this

    fetch('/api/user/sessionStatus', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => response.json())
    .then(sessionData => {
        if (!sessionData.isLoggedIn) throw new Error('User not logged in');
        return fetch(`/api/bankAccount/getConnectedBankAccounts?targetConnectedUserID=cU${sessionData.username}`);
    })
    .then(response => response.json())
    .then(bankAccounts => {
        const dropdown = document.getElementById('account-dropdown');
        dropdown.innerHTML = '<option value="" disabled selected>Select target account</option>';
        
        bankAccounts.forEach(account => {
            if (account.bankAccountID !== currentAccountID) {
                const option = document.createElement('option');
                option.value = account.bankAccountID;
                option.textContent = `${account.bankAccountName} - $${account.balance.toFixed(2)}`;
                dropdown.appendChild(option);
            }
        });
    })
    .catch(error => {
        console.error('Error populating dropdown:', error);
    });
}

function confirmTransferEndpoint() {
    // Get the selected target account ID from dropdown
    const targetAccountID = document.getElementById('account-dropdown').value;
    
    // Get the transfer amount from input
    const transferAmount = parseFloat(document.getElementById('transfer-amount-input').value);

    // Get the current account ID from the hidden input
    const urlParams = new URLSearchParams(window.location.hash.split('?')[1]);
    const sourceAccountID = urlParams.get('bankAccountID');

    if (!targetAccountID || !transferAmount || !sourceAccountID) {
        console.error('Missing required transfer information');
        return;
    }

    // Create query parameters for the transfer
    const params = new URLSearchParams({
        sourceAccountID: sourceAccountID,
        targetAccountID: targetAccountID,
        amount: transferAmount
    });

    // Call the transferFunds endpoint
    fetch(`/api/bankAccount/transferFunds?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Transfer failed');
        }
        return response.json();
    })
    .then(success => {
        if (success) {
            alert('Transfer completed successfully!');
            window.location.hash = 'accounts'; // Redirect back to accounts page
        } else {
            alert('Transfer failed. Please check account balances and try again.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while processing the transfer. Please try again.');
    });
}
