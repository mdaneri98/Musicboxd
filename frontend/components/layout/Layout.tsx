/**
 * Layout Component
 * Main layout wrapper with conditional sidebar
 * Provides consistent structure across all pages
 */

import { ReactNode } from 'react';
import Head from './Head';
import Sidebar from './Sidebar';
import Footer from './Footer';
import { ToastContainer } from '@/components/ui';

interface LayoutProps {
  children: ReactNode;
  title: string;
  showSidebar?: boolean;
  showFooter?: boolean;
}

const Layout = ({
  children,
  title,
  showSidebar = true,
  showFooter = true,
}: LayoutProps) => {
  return (
    <>
      <Head title={title} />
      <div className="main-container">
        {showSidebar && <Sidebar />}
        <main className="content-wrapper">
          {children}
        </main>
      </div>
      {showFooter && <Footer />}
      <ToastContainer />
    </>
  );
};

export default Layout;

