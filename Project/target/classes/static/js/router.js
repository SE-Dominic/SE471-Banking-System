//load a template dynamically
function loadTemplate(templateName) {
    const app = document.getElementById('app');
    
    // Define protected templates that require authentication
    const protectedTemplates = ['dashboard', 'budget', 'profile', 'transactions', 'transactionForm', 'withdrawForm'];

    // Check if the requested template requires authentication
    if (protectedTemplates.includes(templateName)) {
        // Verify session before loading protected template
        fetch('/api/user/sessionStatus', {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Session validation failed');
            }
            return response.json();
        })
        .then(data => {
            if (data.isLoggedIn) {
                // User is authenticated, load the requested template
                loadRequestedTemplate(templateName);
            } else {
                // User is not authenticated, redirect to login
                
                window.location.hash = 'login';
                alert('User not authenticated, redirecting to login...');
            }
        })
        .catch(error => {
            console.error('Session check error:', error);
            window.location.hash = 'login';
        });
    } else {
        // Template doesn't require authentication, load it directly
        loadRequestedTemplate(templateName);
    }
}

// Helper function to load the actual template
function loadRequestedTemplate(templateName) {
    const app = document.getElementById('app');
    
    const [baseTemplateName, queryParams] = templateName.split('?');
    
    // Strip .html for consistent comparison
    const baseTemplateKey = baseTemplateName.replace('.html', ''); // ✅ add this
    
    const templatePath = baseTemplateName.endsWith('.html') ? baseTemplateName : `${baseTemplateName}.html`;
    
    fetch(`/templates/${templatePath}`)
        .then(response => {
            if (!response.ok) throw new Error(`Failed to load template: ${templatePath}`);
            return response.text();
        })
        .then(html => {
            app.innerHTML = html;

            if (baseTemplateKey === 'accounts') {                    // ✅ use baseTemplateKey
                getAllConnectedBankAccountsEndpoint();
            } else if (baseTemplateKey === 'profile') {              // ✅
                loadProfileInfo();
            } else if (baseTemplateKey === 'transactionForm') {      // ✅ will now match
                setTimeout(() => {
                    if (queryParams) {
                        const urlParams = new URLSearchParams(queryParams);
                        const bankAccountID = urlParams.get('bankAccountID');
                        
                        if (bankAccountID) {
                            const hiddenInput = document.createElement('input');
                            hiddenInput.type = 'hidden';
                            hiddenInput.id = 'current-bank-account-id';
                            hiddenInput.value = bankAccountID;
                            document.getElementById('transaction-form').appendChild(hiddenInput);
                            
                            populateAccountDropdown(bankAccountID);
                        }
                    }
                }, 200);
            }
        })
        .catch(error => {
            console.error(error);
            app.innerHTML = `<p>Error loading page: ${templatePath}</p>`;
        });
}

//handles navigation and update the URL hash
document.addEventListener('click', (event) => {
    const target = event.target;
    if (target.tagName === 'A' && target.dataset.page) {
        event.preventDefault();
        const page = target.dataset.page;

        // Update the URL hash
        window.location.hash = page;
    }
});

//handles hash changes to load the correct template
window.addEventListener('hashchange', () => {
    const page = window.location.hash.slice(1) || 'frontpage'; // Default to 'frontpage'
    loadTemplate(page);
});

//load the default page based on the current hash
const initialPage = window.location.hash.slice(1) || 'frontpage';
loadTemplate(initialPage);